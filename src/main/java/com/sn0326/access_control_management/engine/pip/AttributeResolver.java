package com.sn0326.access_control_management.engine.pip;

import com.sn0326.access_control_management.domain.resource.Resource;
import com.sn0326.access_control_management.domain.user.User;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

/**
 * PIP（Policy Information Point）
 * User / Resource / 環境情報から AccessContext を組み立てる。
 *
 * <p>固定カラム設計のため、リフレクションを使わず明示的にマッピングする。
 * ポリシー条件で参照できる属性名はここで定義した key と一致させること。
 */
@Component
public class AttributeResolver {

    /**
     * AccessContext を組み立てる。
     * request が null の場合は環境属性を空にする（テスト用途）。
     */
    public AccessContext resolve(User user, Resource resource, String actionName, HttpServletRequest request) {
        return new AccessContext(
                resolveUserAttrs(user),
                resolveResourceAttrs(resource),
                resolveEnvAttrs(request),
                actionName
        );
    }

    /**
     * ユーザー属性マップを作成する。
     * key はポリシー条件の left_attr_name / right_attr_name と一致させる。
     */
    public Map<String, String> resolveUserAttrs(User user) {
        Map<String, String> attrs = new HashMap<>();
        attrs.put("department",      user.getDepartment());
        attrs.put("role",            user.getRole());
        attrs.put("clearance_level", String.valueOf(user.getClearanceLevel()));
        return attrs;
    }

    /**
     * リソース属性マップを作成する。
     */
    public Map<String, String> resolveResourceAttrs(Resource resource) {
        Map<String, String> attrs = new HashMap<>();
        attrs.put("resource_type",     resource.getResourceType());
        attrs.put("owner_department",  resource.getOwnerDepartment());
        attrs.put("sensitivity_level", String.valueOf(resource.getSensitivityLevel()));
        return attrs;
    }

    /**
     * 環境属性マップを作成する。
     */
    public Map<String, String> resolveEnvAttrs(HttpServletRequest request) {
        Map<String, String> attrs = new HashMap<>();
        attrs.put("hour", String.valueOf(LocalTime.now().getHour()));
        if (request != null) {
            attrs.put("ip_address", request.getRemoteAddr());
        }
        return attrs;
    }
}
