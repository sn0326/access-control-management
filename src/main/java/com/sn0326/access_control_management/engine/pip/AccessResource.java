package com.sn0326.access_control_management.engine.pip;

import java.util.Map;

/**
 * アクセス対象リソースの抽象インターフェース。
 *
 * <h2>ライブラリ設計の意図</h2>
 * Subject と同様に、エンジンはリソースの具体的な型を知らない。
 * Document / ApiEndpoint / DatabaseTable など、アプリ固有のリソース型は
 * このインターフェースを実装してエンジンに渡す。
 *
 * <h2>2つのメソッドの使い分け</h2>
 * <ul>
 *   <li>{@link #getPrimaryAttributes()} — TargetMatcher が policy_targets の RESOURCE カテゴリ照合に使う。
 *       AccessContext に "RESOURCE" キーで格納される。</li>
 *   <li>{@link #getAttributeSources()} — ConditionEvaluator が policy_conditions の
 *       left/right_attr_source で参照する。アプリ側がソース名を自由に定義できる。
 *       例: {"RESOURCE_ATTR": {...}}</li>
 * </ul>
 */
public interface AccessResource {

    /**
     * TargetMatcher が policy_targets の RESOURCE カテゴリ照合に使う主属性。
     * AccessContext には "RESOURCE" キーで格納される。
     */
    Map<String, String> getPrimaryAttributes();

    /**
     * ConditionEvaluator が policy_conditions の left/right_attr_source で参照する属性ソース群。
     * キーはポリシー条件のソース名（例: "RESOURCE_ATTR"）と一致させること。
     */
    Map<String, Map<String, String>> getAttributeSources();
}
