package com.sn0326.access_control_management.domain.user;

import com.sn0326.access_control_management.engine.pip.SubjectAttributeProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * {@link SubjectAttributeProvider} のデフォルト実装。
 *
 * <p>数値IDで users テーブルを検索し、属性マップを返す。
 * 他サービスが独自の {@link SubjectAttributeProvider} を登録する場合は、
 * {@link #supports(String)} で {@code false} を返すようにすることで共存できる。
 */
@Component
@RequiredArgsConstructor
public class DefaultSubjectAttributeProvider implements SubjectAttributeProvider {

    private final UserRepository userRepository;

    /** 数値IDのみ処理する */
    @Override
    public boolean supports(String subjectId) {
        if (subjectId == null) return false;
        try {
            Long.parseLong(subjectId);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    public Map<String, String> resolve(String subjectId) {
        User user = userRepository.findById(Long.parseLong(subjectId))
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + subjectId));

        Map<String, String> attrs = new HashMap<>();
        attrs.put("department",      user.getDepartment());
        attrs.put("role",            user.getRole());
        attrs.put("clearance_level", String.valueOf(user.getClearanceLevel()));
        return attrs;
    }
}
