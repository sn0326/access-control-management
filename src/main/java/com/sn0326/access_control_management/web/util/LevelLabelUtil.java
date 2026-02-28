package com.sn0326.access_control_management.web.util;

import org.springframework.stereotype.Component;

/**
 * 機密レベル・クリアランスレベルの数値をわかりやすいラベルに変換するユーティリティ。
 *
 * <p>Thymeleaf テンプレートから {@code @levelLabel.sensitivityLabel(level)} のように呼び出せる。
 *
 * <p>レベル対応表:
 * <pre>
 *   Lv.1 → 公開 / 一般
 *   Lv.2 → 社内限定 / 社員
 *   Lv.3 → 部門限定 / リーダー
 *   Lv.4 → 機密 / 管理職
 *   Lv.5 → 極秘 / 管理者
 * </pre>
 */
@Component("levelLabel")
public class LevelLabelUtil {

    // ---------------------------------------------------------------
    // 機密レベル（リソース側: sensitivity_level）
    // ---------------------------------------------------------------

    /** 機密レベルの日本語ラベルを返す。 */
    public String sensitivityLabel(int level) {
        return switch (level) {
            case 1 -> "公開";
            case 2 -> "社内限定";
            case 3 -> "部門限定";
            case 4 -> "機密";
            case 5 -> "極秘";
            default -> "不明";
        };
    }

    /** 機密レベルに対応する Bootstrap バッジクラスを返す（低=緑、高=黒）。 */
    public String sensitivityBadgeClass(int level) {
        return switch (level) {
            case 1 -> "bg-success";
            case 2 -> "bg-primary";
            case 3 -> "bg-warning text-dark";
            case 4 -> "bg-danger";
            case 5 -> "bg-dark";
            default -> "bg-secondary";
        };
    }

    // ---------------------------------------------------------------
    // クリアランスレベル（ユーザー側: clearance_level）
    // ---------------------------------------------------------------

    /** クリアランスレベルの日本語ラベルを返す。 */
    public String clearanceLabel(int level) {
        return switch (level) {
            case 1 -> "一般";
            case 2 -> "社員";
            case 3 -> "リーダー";
            case 4 -> "管理職";
            case 5 -> "管理者";
            default -> "不明";
        };
    }

    /** クリアランスレベルに対応する Bootstrap バッジクラスを返す（低=緑、高=黒）。 */
    public String clearanceBadgeClass(int level) {
        return switch (level) {
            case 1 -> "bg-success";
            case 2 -> "bg-primary";
            case 3 -> "bg-warning text-dark";
            case 4 -> "bg-danger";
            case 5 -> "bg-dark";
            default -> "bg-secondary";
        };
    }
}
