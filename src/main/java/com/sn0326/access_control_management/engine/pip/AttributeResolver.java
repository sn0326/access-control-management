package com.sn0326.access_control_management.engine.pip;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * PIP（Policy Information Point）
 *
 * <p>登録された {@link SubjectAttributeProvider} / {@link ResourceAttributeProvider} に
 * 属性解決を委譲し、{@link AccessContext} を組み立てる。
 *
 * <p>アプリケーションは両インタフェースの実装を Spring Bean として登録するだけでよい。
 * このクラス自体はドメインエンティティや特定の DB スキーマに依存しない。
 */
@Component
@RequiredArgsConstructor
public class AttributeResolver {

    private final List<SubjectAttributeProvider> subjectProviders;
    private final List<ResourceAttributeProvider> resourceProviders;

    /**
     * サブジェクト識別子・リソース識別子から {@link AccessContext} を組み立てる。
     *
     * @param subjectId  サブジェクト識別子（数値ID、URN 等）
     * @param resourceId リソース識別子（数値ID、URN 等）
     * @param actionName 実行しようとしているアクション名
     * @param request    HTTP リクエスト（環境属性取得用、null 可）
     */
    public AccessContext resolve(String subjectId, String resourceId,
                                 String actionName, HttpServletRequest request) {
        return new AccessContext(
                resolveSubject(subjectId),
                resolveResource(resourceId),
                resolveEnv(request),
                actionName
        );
    }

    private Map<String, String> resolveSubject(String subjectId) {
        return subjectProviders.stream()
                .filter(p -> p.supports(subjectId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "No SubjectAttributeProvider supports subjectId: " + subjectId))
                .resolve(subjectId);
    }

    private Map<String, String> resolveResource(String resourceId) {
        return resourceProviders.stream()
                .filter(p -> p.supports(resourceId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "No ResourceAttributeProvider supports resourceId: " + resourceId))
                .resolve(resourceId);
    }

    private Map<String, String> resolveEnv(HttpServletRequest request) {
        Map<String, String> attrs = new HashMap<>();
        attrs.put("hour", String.valueOf(LocalTime.now().getHour()));
        if (request != null) {
            attrs.put("ip_address", request.getRemoteAddr());
        }
        return attrs;
    }
}
