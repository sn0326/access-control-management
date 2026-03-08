package com.sn0326.access_control_management.engine.pdp;

import com.sn0326.access_control_management.domain.policy.Policy;
import com.sn0326.access_control_management.domain.policy.PolicyTarget;
import com.sn0326.access_control_management.engine.pip.AccessContext;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * ポリシーのターゲットマッチング。
 *
 * <ul>
 *   <li>同一カテゴリ内の複数行 → OR 条件（いずれか1つでもマッチすればOK）</li>
 *   <li>異なるカテゴリ間 → AND 条件（全カテゴリがマッチしなければならない）</li>
 *   <li>対象カテゴリのターゲットが未定義 → ワイルドカード（常にマッチ）</li>
 * </ul>
 *
 * <h2>ライブラリ設計の意図</h2>
 * target_category は "ACTION" のみ特殊扱い（actionName で直接解決）。
 * それ以外のカテゴリ（SUBJECT / RESOURCE 等）は context.attrs().get(category) で
 * ジェネリックに解決する。
 *
 * AttributeResolver が "SUBJECT" / "RESOURCE" キーで主属性を格納しているため、
 * target_category="SUBJECT" なら attrs.get("SUBJECT") を、
 * target_category="RESOURCE" なら attrs.get("RESOURCE") を引くだけでよい。
 * 新しい主体カテゴリを追加してもこのクラスは変更不要。
 */
@Component
public class TargetMatcher {

    public boolean matches(Policy policy, AccessContext context) {
        List<PolicyTarget> targets = policy.getTargets();
        if (targets.isEmpty()) {
            return true; // ターゲット未定義 = 全対象
        }

        // カテゴリごとにグループ化
        Map<String, List<PolicyTarget>> byCategory = targets.stream()
                .collect(Collectors.groupingBy(PolicyTarget::getTargetCategory));

        // 全カテゴリが AND 条件で成立する必要がある
        for (Map.Entry<String, List<PolicyTarget>> entry : byCategory.entrySet()) {
            String category = entry.getKey();
            List<PolicyTarget> categoryTargets = entry.getValue();

            // 同一カテゴリ内は OR 条件
            boolean categoryMatches = categoryTargets.stream()
                    .anyMatch(t -> matchesSingle(t, category, context));

            if (!categoryMatches) {
                return false;
            }
        }
        return true;
    }

    private boolean matchesSingle(PolicyTarget target, String category, AccessContext context) {
        String actualValue = resolveActualValue(target.getAttrName(), category, context);
        if (actualValue == null) {
            return false;
        }
        return AttributeComparator.compare(actualValue, target.getOperator(), target.getAttrValue());
    }

    /**
     * "ACTION" のみ特殊解決。それ以外は target_category をそのまま attrs のキーとして引く。
     * SUBJECT → attrs.get("SUBJECT")、RESOURCE → attrs.get("RESOURCE") となる。
     */
    private String resolveActualValue(String attrName, String category, AccessContext context) {
        if ("ACTION".equals(category)) {
            return "name".equals(attrName) ? context.actionName() : null;
        }
        return Optional.ofNullable(context.attrs().get(category))
                       .map(m -> m.get(attrName))
                       .orElse(null);
    }
}
