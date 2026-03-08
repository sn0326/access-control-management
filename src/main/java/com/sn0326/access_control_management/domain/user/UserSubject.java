package com.sn0326.access_control_management.domain.user;

import com.sn0326.access_control_management.engine.pip.Subject;

import java.util.Map;

/**
 * User エンティティを Subject インターフェースに適合させるアダプター。
 *
 * <h2>アプリ層の実装</h2>
 * このクラスはエンジンライブラリには属さず、このアプリケーション固有の実装。
 * ポリシー条件で "USER_ATTR" をソース名として使うことをここで規約として定めている。
 *
 * <h2>別の主体を追加する場合</h2>
 * 例えばグループを主体にしたい場合は GroupSubject を同様に実装すればよく、
 * エンジン側（ConditionEvaluator / TargetMatcher / AttributeResolver）は変更不要。
 *
 * ユーザーとグループの両属性をポリシー条件で参照したい場合は、
 * getAttributeSources() で "USER_ATTR" と "GROUP_ATTR" を両方返す実装にすればよい:
 * <pre>
 * public class UserWithGroupSubject implements Subject {
 *     public Map&lt;String, Map&lt;String, String&gt;&gt; getAttributeSources() {
 *         return Map.of(
 *             "USER_ATTR",  Map.of("department", user.getDepartment(), ...),
 *             "GROUP_ATTR", Map.of("clearance_level", group.getClearanceLevel(), ...)
 *         );
 *     }
 * }
 * </pre>
 */
public class UserSubject implements Subject {

    private final User user;

    public UserSubject(User user) {
        this.user = user;
    }

    /**
     * TargetMatcher が policy_targets の SUBJECT カテゴリ照合に使う主属性。
     * ポリシーの target_category="SUBJECT" で参照できる属性名はここで定義したキーと一致させること。
     */
    @Override
    public Map<String, String> getPrimaryAttributes() {
        return Map.of(
                "department",      user.getDepartment(),
                "role",            user.getRole(),
                "clearance_level", String.valueOf(user.getClearanceLevel())
        );
    }

    /**
     * ConditionEvaluator が left/right_attr_source="USER_ATTR" の条件を解決するために使う。
     * ポリシー条件で参照できる属性名はこのMapのキーと一致させること。
     */
    @Override
    public Map<String, Map<String, String>> getAttributeSources() {
        return Map.of("USER_ATTR", getPrimaryAttributes());
    }
}
