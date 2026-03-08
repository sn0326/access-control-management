package com.sn0326.access_control_management.engine.pdp;

import com.sn0326.access_control_management.domain.policy.PolicyCondition;
import com.sn0326.access_control_management.engine.pip.AccessContext;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

/**
 * ポリシー条件の評価。
 *
 * <h2>ライブラリ設計の意図</h2>
 * attr_source の解決は "CONST" のみ特殊扱いし、それ以外はすべて
 * context.attrs().get(source) でジェネリックに引く。
 * これにより、新しいソース名（"GROUP_ATTR" 等）が追加されても
 * このクラスは変更不要。ソース名はアプリ側の Subject / AccessResource 実装が定義する。
 */
@Component
public class ConditionEvaluator {

    public boolean evaluate(PolicyCondition condition, AccessContext context) {
        String leftValue  = resolveValue(condition.getLeftAttrSource(),  condition.getLeftAttrName(),  context);
        String rightValue = resolveValue(condition.getRightAttrSource(), condition.getRightAttrName(), context);

        if (leftValue == null || rightValue == null) {
            return false; // 属性が存在しない場合は条件不成立
        }

        return AttributeComparator.compare(leftValue, condition.getOperator(), rightValue);
    }

    /**
     * CONST はattr_name自体が値。
     * それ以外は context.attrs() のソース名をキーとしてジェネリックに解決する。
     * 未知のソース名や属性名が存在しない場合はnullを返す（条件不成立）。
     */
    private String resolveValue(String source, String name, AccessContext context) {
        if ("CONST".equals(source)) {
            return name;
        }
        return Optional.ofNullable(context.attrs().get(source))
                       .map(m -> m.get(name))
                       .orElse(null);
    }
}
