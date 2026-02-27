package com.sn0326.access_control_management.domain.resource;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "resources")
@Getter
@Setter
@NoArgsConstructor
public class Resource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String name;

    /** リソース種別 (DOCUMENT / API / REPORT / DATABASE) */
    @Column(nullable = false, length = 100)
    private String resourceType;

    /** このリソースを所有する部署 */
    @Column(nullable = false, length = 100)
    private String ownerDepartment;

    /** 機密レベル 1(低) 〜 5(高) */
    @Column(nullable = false)
    private int sensitivityLevel;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
