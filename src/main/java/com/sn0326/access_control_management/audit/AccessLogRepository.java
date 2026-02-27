package com.sn0326.access_control_management.audit;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccessLogRepository extends JpaRepository<AccessLog, Long> {

    List<AccessLog> findTop50ByOrderByEvaluatedAtDesc();
}
