package com.sn0326.access_control_management.engine.pip;

import java.util.Map;

/**
 * PDP が評価に使うコンテキスト情報。
 * PIP（AttributeResolver）が組み立てて PDP に渡す。
 *
 * @param userAttrs     ユーザー属性 (key=カラム名, value=文字列化した値)
 *                      例: {department=engineering, role=developer, clearance_level=2}
 * @param resourceAttrs リソース属性 (key=カラム名, value=文字列化した値)
 *                      例: {resource_type=DOCUMENT, owner_department=engineering, sensitivity_level=2}
 * @param envAttrs      環境属性 (key=属性名, value=文字列化した値)
 *                      例: {hour=14, ip_address=192.168.1.1}
 * @param actionName    実行しようとしているアクション名 (READ / WRITE / DELETE / EXECUTE)
 */
public record AccessContext(
        Map<String, String> userAttrs,
        Map<String, String> resourceAttrs,
        Map<String, String> envAttrs,
        String actionName
) {
}
