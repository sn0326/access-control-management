package com.sn0326.access_control_management.domain.policy;

import com.sn0326.access_control_management.engine.pdp.PolicyStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * {@link PolicyStore} の JPA 実装。
 *
 * <p>エンジンコアは {@link PolicyStore} に依存するため、
 * このクラスを別実装（インメモリ等）に差し替えるだけでエンジンを再利用できる。
 */
@Component
@RequiredArgsConstructor
public class JpaPolicyStore implements PolicyStore {

    private final PolicyRepository policyRepository;

    @Override
    public List<Policy> findEnabledOrderByPriorityDesc() {
        return policyRepository.findAllEnabledOrderByPriorityDesc();
    }
}
