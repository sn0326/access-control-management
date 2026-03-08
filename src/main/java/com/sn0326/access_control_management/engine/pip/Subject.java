package com.sn0326.access_control_management.engine.pip;

import java.util.Map;

/**
 * アクセス主体（Subject）の抽象インターフェース。
 *
 * <h2>ライブラリ設計の意図</h2>
 * エンジンは「誰が」アクセス主体なのかを直接知らない。
 * User / Group / ServiceAccount など、アプリ固有の主体型はこのインターフェースを実装して
 * エンジンに渡す。エンジンはこのインターフェース越しに属性を取得するだけなので、
 * 主体の種類が増えてもエンジン側（ConditionEvaluator / TargetMatcher 等）は変更不要。
 *
 * <h2>2つのメソッドの使い分け</h2>
 * <ul>
 *   <li>{@link #getPrimaryAttributes()} — TargetMatcher が policy_targets の SUBJECT カテゴリ照合に使う。
 *       AccessContext に "SUBJECT" キーで格納される。</li>
 *   <li>{@link #getAttributeSources()} — ConditionEvaluator が policy_conditions の
 *       left/right_attr_source で参照する。アプリ側がソース名を自由に定義できる。
 *       例: {"USER_ATTR": {...}, "GROUP_ATTR": {...}}</li>
 * </ul>
 *
 * <h2>実装例</h2>
 * <pre>
 * // User を主体にする場合
 * public class UserSubject implements Subject { ... }
 *
 * // User + 所属 Group の両属性を条件で参照したい場合
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
public interface Subject {

    /**
     * TargetMatcher が policy_targets の SUBJECT カテゴリ照合に使う主属性。
     * AccessContext には "SUBJECT" キーで格納される。
     */
    Map<String, String> getPrimaryAttributes();

    /**
     * ConditionEvaluator が policy_conditions の left/right_attr_source で参照する属性ソース群。
     * キーはポリシー条件のソース名（例: "USER_ATTR", "GROUP_ATTR"）と一致させること。
     */
    Map<String, Map<String, String>> getAttributeSources();
}
