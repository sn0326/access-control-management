package com.sn0326.access_control_management.engine.pdp;

import com.sn0326.access_control_management.domain.policy.PolicyCondition;
import com.sn0326.access_control_management.engine.pip.AccessContext;
import org.springframework.stereotype.Component;

/**
 * ポリシー条件の評価。
 *
 * <p>left/right それぞれの attr_source に従って値を解決し、
 * operator で比較した結果を返す。
 */
@Component
public class ConditionEvaluator {

    public boolean evaluate(PolicyCondition condition, AccessContext context) {
        String leftValue  = resolveValue(condition.getLeftAttrSource(),  condition.getLeftAttrName(),  context);
        String rightValue = resolveValue(condition.getRightAttrSource(), condition.getRightAttrName(), context);

        if (leftValue == null || rightValue == null) {
            return false; // 属性が存在しない場合は条件不成立
        }

        return compare(leftValue, condition.getOperator(), rightValue);
    }

    /**
     * attr_source に応じて値を解決する。
     * CONST の場合は attr_name 自体が値（定数）。
     */
    private String resolveValue(String source, String name, AccessContext context) {
        return switch (source) {
            case "USER_ATTR"     -> context.userAttrs().get(name);
            case "RESOURCE_ATTR" -> context.resourceAttrs().get(name);
            case "ENV_ATTR"      -> context.envAttrs().get(name);
            case "CONST"         -> name; // name IS the constant value
            default -> null;
        };
    }

    private boolean compare(String left, String operator, String right) {
        return switch (operator) {
            case "EQ"       -> left.equalsIgnoreCase(right);
            case "NEQ"      -> !left.equalsIgnoreCase(right);
            case "CONTAINS" -> left.contains(right);
            case "GT", "GTE", "LT", "LTE" -> compareNumeric(left, operator, right);
            default -> false;
        };
    }

    private boolean compareNumeric(String left, String operator, String right) {
        try {
            double l = Double.parseDouble(left);
            double r = Double.parseDouble(right);
            return switch (operator) {
                case "GT"  -> l > r;
                case "GTE" -> l >= r;
                case "LT"  -> l < r;
                case "LTE" -> l <= r;
                default -> false;
            };
        } catch (NumberFormatException ex) {
            return false;
        }
    }
}
