package com.sn0326.access_control_management.engine.pdp;

import java.util.List;

/**
 * 属性値の比較ロジック。
 *
 * <p>{@link TargetMatcher} と {@link ConditionEvaluator} で重複していた
 * compare / compareNumeric を一箇所にまとめたユーティリティ。
 *
 * <p>サポートする演算子:
 * <ul>
 *   <li>EQ / NEQ       - 文字列の等値比較（大文字小文字無視）</li>
 *   <li>CONTAINS       - 部分一致</li>
 *   <li>IN             - カンマ区切りリストに含まれるか</li>
 *   <li>GT / GTE / LT / LTE - 数値比較</li>
 * </ul>
 */
final class AttributeComparator {

    private AttributeComparator() {}

    static boolean compare(String left, String operator, String right) {
        return switch (operator) {
            case "EQ"       -> left.equalsIgnoreCase(right);
            case "NEQ"      -> !left.equalsIgnoreCase(right);
            case "CONTAINS" -> left.contains(right);
            case "IN"       -> List.of(right.split(",")).contains(left);
            case "GT", "GTE", "LT", "LTE" -> compareNumeric(left, operator, right);
            default -> false;
        };
    }

    private static boolean compareNumeric(String left, String operator, String right) {
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
