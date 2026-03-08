package com.sn0326.access_control_management.domain.resource;

import com.sn0326.access_control_management.engine.pip.ResourceAttributeProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * このシステム（access-control-management）のリソースを対象とする
 * {@link ResourceAttributeProvider} 実装。
 *
 * <p>担当する識別子の形式: {@value #URN_PREFIX}{resourceId}
 * （例: {@code urn:acm:resource:10}）
 *
 * <p>他システムや別エンティティのリソースを扱うプロバイダは、
 * 異なるURNプレフィックスを宣言することでこのプロバイダと共存できる。
 */
@Component
@RequiredArgsConstructor
public class DefaultResourceAttributeProvider implements ResourceAttributeProvider {

    /** このプロバイダが担当するURNプレフィックス */
    public static final String URN_PREFIX = "urn:acm:resource:";

    private final ResourceRepository resourceRepository;

    @Override
    public boolean supports(String resourceId) {
        return resourceId != null && resourceId.startsWith(URN_PREFIX);
    }

    @Override
    public Map<String, String> resolve(String resourceId) {
        long id = parseId(resourceId);
        Resource resource = resourceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Resource not found: " + resourceId));

        Map<String, String> attrs = new HashMap<>();
        attrs.put("resource_type",     resource.getResourceType());
        attrs.put("owner_department",  resource.getOwnerDepartment());
        attrs.put("sensitivity_level", String.valueOf(resource.getSensitivityLevel()));
        return attrs;
    }

    private long parseId(String resourceId) {
        try {
            return Long.parseLong(resourceId.substring(URN_PREFIX.length()));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid resource URN: " + resourceId, e);
        }
    }
}
