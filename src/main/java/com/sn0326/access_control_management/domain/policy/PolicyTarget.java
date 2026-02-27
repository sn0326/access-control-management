package com.sn0326.access_control_management.domain.policy;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * ポリシーの適用範囲を定義する。
 *
 * <p>target_category ごとに対象を絞り込む。
 * <ul>
 *   <li>SUBJECT  → attr_name: department / role / clearance_level</li>
 *   <li>RESOURCE → attr_name: resource_type / owner_department / sensitivity_level</li>
 *   <li>ACTION   → attr_name: name (READ / WRITE / DELETE / EXECUTE)</li>
 * </ul>
 *
 * <p>同一カテゴリの複数行は OR、異なるカテゴリ間は AND で評価する。
 * 定義のないカテゴリはワイルドカード（全対象）。
 */
@Entity
@Table(name = "policy_targets")
@Getter
@Setter
@NoArgsConstructor
public class PolicyTarget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "policy_id", nullable = false)
    private Policy policy;

    /** SUBJECT / RESOURCE / ACTION */
    @Column(nullable = false, length = 20)
    private String targetCategory;

    /** 評価対象の属性名 */
    @Column(nullable = false, length = 100)
    private String attrName;

    /** EQ / NEQ / GT / LT / GTE / LTE / IN */
    @Column(nullable = false, length = 20)
    private String operator;

    /** 比較値 */
    @Column(nullable = false, length = 500)
    private String attrValue;
}
