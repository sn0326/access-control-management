package com.sn0326.access_control_management.domain.user;

import com.sn0326.access_control_management.engine.pip.SubjectAttributeProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * このシステム（access-control-management）のユーザーを対象とする
 * {@link SubjectAttributeProvider} 実装。
 *
 * <p>担当する識別子の形式: {@value #URN_PREFIX}{userId}
 * （例: {@code urn:acm:user:42}）
 *
 * <p>他システムや別エンティティのサブジェクトを扱うプロバイダは、
 * 異なるURNプレフィックスを宣言することでこのプロバイダと共存できる。
 */
@Component
@RequiredArgsConstructor
public class DefaultSubjectAttributeProvider implements SubjectAttributeProvider {

    /** このプロバイダが担当するURNプレフィックス */
    public static final String URN_PREFIX = "urn:acm:user:";

    private final UserRepository userRepository;

    @Override
    public boolean supports(String subjectId) {
        return subjectId != null && subjectId.startsWith(URN_PREFIX);
    }

    @Override
    public Map<String, String> resolve(String subjectId) {
        long id = parseId(subjectId);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + subjectId));

        Map<String, String> attrs = new HashMap<>();
        attrs.put("department",      user.getDepartment());
        attrs.put("role",            user.getRole());
        attrs.put("clearance_level", String.valueOf(user.getClearanceLevel()));
        return attrs;
    }

    private long parseId(String subjectId) {
        try {
            return Long.parseLong(subjectId.substring(URN_PREFIX.length()));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid subject URN: " + subjectId, e);
        }
    }
}
