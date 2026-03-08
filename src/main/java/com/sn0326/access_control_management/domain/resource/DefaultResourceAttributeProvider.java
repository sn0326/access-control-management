package com.sn0326.access_control_management.domain.resource;

import com.sn0326.access_control_management.engine.pip.ResourceAttributeProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * {@link ResourceAttributeProvider} のデフォルト実装。
 *
 * <p>数値IDで resources テーブルを検索し、属性マップを返す。
 * 他サービスが独自の {@link ResourceAttributeProvider} を登録する場合は、
 * {@link #supports(String)} で {@code false} を返すようにすることで共存できる。
 */
@Component
@RequiredArgsConstructor
public class DefaultResourceAttributeProvider implements ResourceAttributeProvider {

    private final ResourceRepository resourceRepository;

    /** 数値IDのみ処理する */
    @Override
    public boolean supports(String resourceId) {
        if (resourceId == null) return false;
        try {
            Long.parseLong(resourceId);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    public Map<String, String> resolve(String resourceId) {
        Resource resource = resourceRepository.findById(Long.parseLong(resourceId))
                .orElseThrow(() -> new IllegalArgumentException("Resource not found: " + resourceId));

        Map<String, String> attrs = new HashMap<>();
        attrs.put("resource_type",     resource.getResourceType());
        attrs.put("owner_department",  resource.getOwnerDepartment());
        attrs.put("sensitivity_level", String.valueOf(resource.getSensitivityLevel()));
        return attrs;
    }
}
