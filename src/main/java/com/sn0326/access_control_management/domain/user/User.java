package com.sn0326.access_control_management.domain.user;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String username;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    /** 所属部署 (engineering / hr / finance / sales / it) */
    @Column(nullable = false, length = 100)
    private String department;

    /** ロール (admin / manager / developer / viewer) */
    @Column(nullable = false, length = 50)
    private String role;

    /** クリアランスレベル 1(低) 〜 5(高) */
    @Column(nullable = false)
    private int clearanceLevel;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
