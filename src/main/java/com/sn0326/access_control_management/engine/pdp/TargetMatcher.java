package com.sn0326.access_control_management.engine.pdp;

import com.sn0326.access_control_management.domain.policy.Policy;
import com.sn0326.access_control_management.domain.policy.PolicyTarget;
import com.sn0326.access_control_management.engine.pip.AccessContext;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * ポリシーのターゲットマッチング。
 *
 * <p>対象カテゴリごとにターゲット行を評価する。
 * <ul>
 *   <li>同一カテゴリ内の複数行 → OR 条件（いずれか1つでもマッチすればOK）</li>
 *   <li>異なるカテゴリ間 → AND 条件（全カテゴリがマッチしなければならない）</li>
 *   <li>対象カテゴリのターゲットが未定義 → ワイルドカード（常にマッチ）</li>
 * </ul>
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
        return compare(actualValue, target.getOperator(), target.getAttrValue());
    }

    private String resolveActualValue(String attrName, String category, AccessContext context) {
        return switch (category) {
            case "SUBJECT"  -> context.userAttrs().get(attrName);
            case "RESOURCE" -> context.resourceAttrs().get(attrName);
            case "ACTION"   -> "name".equals(attrName) ? context.actionName() : null;
            default -> null;
        };
    }

    private boolean compare(String actual, String operator, String expected) {
        return switch (operator) {
            case "EQ"  -> actual.equalsIgnoreCase(expected);
            case "NEQ" -> !actual.equalsIgnoreCase(expected);
            case "IN"  -> List.of(expected.split(",")).contains(actual);
            case "GT", "GTE", "LT", "LTE" -> compareNumeric(actual, operator, expected);
            default -> false;
        };
    }

    private boolean compareNumeric(String actual, String operator, String expected) {
        try {
            double a = Double.parseDouble(actual);
            double e = Double.parseDouble(expected);
            return switch (operator) {
                case "GT"  -> a > e;
                case "GTE" -> a >= e;
                case "LT"  -> a < e;
                case "LTE" -> a <= e;
                default -> false;
            };
        } catch (NumberFormatException ex) {
            return false;
        }
    }
}
