package com.sn0326.access_control_management.engine.pip;

import java.util.Map;

/**
 * サブジェクト（ユーザー等）の属性を解決するプロバイダのインタフェース。
 *
 * <p>このコンポーネントを利用するアプリケーションは、このインタフェースを実装した
 * Bean を Spring コンテナに登録することで、独自のサブジェクト属性解決ロジックを注入できる。
 *
 * <p>複数の実装が登録された場合、{@link #supports(String)} が {@code true} を返す
 * 最初のプロバイダが使用される。
 */
public interface SubjectAttributeProvider {

    /**
     * 指定されたサブジェクト識別子をこのプロバイダが処理できるか判定する。
     *
     * @param subjectId サブジェクト識別子（数値ID、URN 等）
     * @return 処理できる場合 {@code true}
     */
    boolean supports(String subjectId);

    /**
     * サブジェクト識別子から属性マップを解決する。
     *
     * <p>返却する Map のキーはポリシー条件の {@code left_attr_name} / {@code right_attr_name}
     * と一致させること。
     *
     * @param subjectId サブジェクト識別子
     * @return 属性マップ（key=属性名, value=文字列化した値）
     */
    Map<String, String> resolve(String subjectId);
}
