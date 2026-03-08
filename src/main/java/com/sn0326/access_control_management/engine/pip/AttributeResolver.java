package com.sn0326.access_control_management.engine.pip;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

/**
 * PIP（Policy Information Point）
 * Subject / AccessResource / 環境情報から AccessContext を組み立てる。
 *
 * <h2>ライブラリ設計の意図</h2>
 * このクラスはエンジンライブラリの一部であり、具体的な User や Resource を直接知らない。
 * Subject / AccessResource インターフェース経由で属性を取得するため、
 * アプリ側で主体やリソースの種類を追加してもこのクラスは変更不要。
 *
 * <h2>AccessContext への格納ルール</h2>
 * <ul>
 *   <li>"SUBJECT"  キー — Subject.getPrimaryAttributes() を格納。TargetMatcher の SUBJECT カテゴリ用</li>
 *   <li>"RESOURCE" キー — AccessResource.getPrimaryAttributes() を格納。TargetMatcher の RESOURCE カテゴリ用</li>
 *   <li>その他のキー  — Subject/AccessResource の getAttributeSources() が返すエントリをそのまま追加。
 *                       ConditionEvaluator がポリシー条件のソース名として参照する。
 *                       例: "USER_ATTR", "GROUP_ATTR", "RESOURCE_ATTR"</li>
 *   <li>"ENV_ATTR" キー — 実行時の環境属性（時刻・IPアドレス等）</li>
 * </ul>
 */
@Component
public class AttributeResolver {

    /**
     * AccessContext を組み立てる。
     * request が null の場合は環境属性を空にする（テスト用途）。
     */
    public AccessContext resolve(Subject subject, AccessResource resource, String actionName, HttpServletRequest request) {
        Map<String, Map<String, String>> attrs = new HashMap<>();

        // TargetMatcher 用の予約キー（policy_targets の SUBJECT / RESOURCE カテゴリに対応）
        attrs.put("SUBJECT",  subject.getPrimaryAttributes());
        attrs.put("RESOURCE", resource.getPrimaryAttributes());

        // ConditionEvaluator 用（アプリ側が定義したソース名をそのまま追加）
        // Subject 実装が "USER_ATTR" や "GROUP_ATTR" など自由にソースを定義できる
        attrs.putAll(subject.getAttributeSources());
        attrs.putAll(resource.getAttributeSources());

        attrs.put("ENV_ATTR", resolveEnvAttrs(request));

        return new AccessContext(attrs, actionName);
    }

    private Map<String, String> resolveEnvAttrs(HttpServletRequest request) {
        Map<String, String> attrs = new HashMap<>();
        attrs.put("hour", String.valueOf(LocalTime.now().getHour()));
        if (request != null) {
            attrs.put("ip_address", request.getRemoteAddr());
        }
        return attrs;
    }
}
