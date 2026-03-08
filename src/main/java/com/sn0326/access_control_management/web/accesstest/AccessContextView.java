package com.sn0326.access_control_management.web.accesstest;

import com.sn0326.access_control_management.engine.pip.AccessContext;

import java.util.Map;

/**
 * /access-test UI 向けに AccessContext の内容を展開したビューモデル。
 *
 * <p>エンジン内部の {@link AccessContext} をビュー層に直接露出させず、
 * キー名の解決責務をコントローラ層に閉じ込めるために使用する。
 */
public record AccessContextView(
        Map<String, String> userAttrs,
        Map<String, String> resourceAttrs,
        Map<String, String> envAttrs) {

    public static AccessContextView from(AccessContext ctx) {
        return new AccessContextView(
                ctx.attrs().getOrDefault("USER_ATTR",     Map.of()),
                ctx.attrs().getOrDefault("RESOURCE_ATTR", Map.of()),
                ctx.attrs().getOrDefault("ENV_ATTR",      Map.of())
        );
    }
}
