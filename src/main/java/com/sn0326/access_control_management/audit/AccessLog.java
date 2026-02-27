package com.sn0326.access_control_management.audit;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "access_logs")
@Getter
@Setter
@NoArgsConstructor
public class AccessLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long resourceId;

    @Column(nullable = false, length = 100)
    private String actionName;

    /** PERMIT / DENY / NOT_APPLICABLE */
    @Column(nullable = false, length = 20)
    private String decision;

    /** 判定に使われたポリシーのID（任意） */
    private Long matchedPolicyId;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime evaluatedAt;
}
