package com.sn0326.access_control_management.domain.resource;

import com.sn0326.access_control_management.engine.pip.AccessResource;

import java.util.Map;

/**
 * Resource エンティティを AccessResource インターフェースに適合させるアダプター。
 *
 * <h2>アプリ層の実装</h2>
 * このクラスはエンジンライブラリには属さず、このアプリケーション固有の実装。
 * ポリシー条件で "RESOURCE_ATTR" をソース名として使うことをここで規約として定めている。
 *
 * <h2>別のリソース種別を追加する場合</h2>
 * 例えば ApiEndpoint など別のリソース型を追加したい場合は ApiEndpointResource を
 * 同様に実装すればよく、エンジン側は変更不要。
 */
public class ResourceAdapter implements AccessResource {

    private final Resource resource;

    public ResourceAdapter(Resource resource) {
        this.resource = resource;
    }

    /**
     * TargetMatcher が policy_targets の RESOURCE カテゴリ照合に使う主属性。
     * ポリシーの target_category="RESOURCE" で参照できる属性名はここで定義したキーと一致させること。
     */
    @Override
    public Map<String, String> getPrimaryAttributes() {
        return Map.of(
                "resource_type",     resource.getResourceType(),
                "owner_department",  resource.getOwnerDepartment(),
                "sensitivity_level", String.valueOf(resource.getSensitivityLevel())
        );
    }

    /**
     * ConditionEvaluator が left/right_attr_source="RESOURCE_ATTR" の条件を解決するために使う。
     * ポリシー条件で参照できる属性名はこのMapのキーと一致させること。
     */
    @Override
    public Map<String, Map<String, String>> getAttributeSources() {
        return Map.of("RESOURCE_ATTR", getPrimaryAttributes());
    }
}
