package com.sn0326.access_control_management.domain.policy;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PolicyRepository extends JpaRepository<Policy, Long> {

    /** 有効なポリシーを優先度の降順で取得（PDP で使用） */
    @Query("SELECT p FROM Policy p WHERE p.enabled = true ORDER BY p.priority DESC")
    List<Policy> findAllEnabledOrderByPriorityDesc();
}
