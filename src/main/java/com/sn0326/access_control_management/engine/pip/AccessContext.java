package com.sn0326.access_control_management.engine.pip;

import java.util.Map;

/**
 * PDP が評価に使うコンテキスト情報。
 * PIP（AttributeResolver）が組み立てて PDP に渡す。
 *
 * <h2>ライブラリ設計の意図</h2>
 * attrs は "ソース名 → (属性名 → 値)" の二重 Map として汎用化している。
 * エンジンはソース名の意味を知らず、ポリシー条件で指定されたソース名を
 * そのままキーとして値を引くだけ。
 * これにより、新しい属性ソース（GROUP_ATTR 等）を追加してもエンジンは変更不要。
 *
 * <h2>予約済みキー</h2>
 * 以下のキーは TargetMatcher が policy_targets のカテゴリ照合に使うため予約されている。
 * AttributeResolver が自動的に格納するので、アプリ側が意識する必要はない。
 * <ul>
 *   <li>"SUBJECT"  — Subject.getPrimaryAttributes() の内容。policy_targets の SUBJECT カテゴリに対応</li>
 *   <li>"RESOURCE" — AccessResource.getPrimaryAttributes() の内容。policy_targets の RESOURCE カテゴリに対応</li>
 * </ul>
 *
 * <h2>アプリ側が自由に定義するキー</h2>
 * Subject.getAttributeSources() / AccessResource.getAttributeSources() が返すキーが
 * そのまま格納される。ConditionEvaluator はこれらのキーで条件のソースを解決する。
 * 例: "USER_ATTR", "GROUP_ATTR", "RESOURCE_ATTR", "ENV_ATTR"
 *
 * @param attrs      全属性ソース (ソース名 → (属性名 → 文字列値))
 * @param actionName 実行しようとしているアクション名 (READ / WRITE / DELETE / EXECUTE)
 */
public record AccessContext(
        Map<String, Map<String, String>> attrs,
        String actionName) {

    public Map<String, String> userAttrs() {
        return attrs.getOrDefault("USER_ATTR", Map.of());
    }

    public Map<String, String> resourceAttrs() {
        return attrs.getOrDefault("RESOURCE_ATTR", Map.of());
    }

    public Map<String, String> envAttrs() {
        return attrs.getOrDefault("ENV_ATTR", Map.of());
    }
}
