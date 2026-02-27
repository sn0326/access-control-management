package com.sn0326.access_control_management.domain.policy;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * ポリシーのアクセス可否を決定する条件。
 *
 * <p>同一ポリシーの複数条件は AND で評価する。
 *
 * <p>left/right の attr_source に指定できる値:
 * <ul>
 *   <li>USER_ATTR     → users テーブルの固定カラム名 (department, role, clearance_level)</li>
 *   <li>RESOURCE_ATTR → resources テーブルの固定カラム名 (resource_type, owner_department, sensitivity_level)</li>
 *   <li>ENV_ATTR      → 実行時環境値 (hour, ip_address)</li>
 *   <li>CONST         → 定数値（right_attr_name に値を直接記述）</li>
 * </ul>
 */
@Entity
@Table(name = "policy_conditions")
@Getter
@Setter
@NoArgsConstructor
public class PolicyCondition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "policy_id", nullable = false)
    private Policy policy;

    @Column(nullable = false)
    private int conditionOrder;

    /** USER_ATTR / RESOURCE_ATTR / ENV_ATTR / CONST */
    @Column(nullable = false, length = 20)
    private String leftAttrSource;

    /** 属性名（CONST の場合は値そのもの） */
    @Column(nullable = false, length = 100)
    private String leftAttrName;

    /** EQ / NEQ / GT / LT / GTE / LTE / CONTAINS */
    @Column(nullable = false, length = 20)
    private String operator;

    /** USER_ATTR / RESOURCE_ATTR / ENV_ATTR / CONST */
    @Column(nullable = false, length = 20)
    private String rightAttrSource;

    /** 属性名（CONST の場合は値そのもの） */
    @Column(nullable = false, length = 100)
    private String rightAttrName;
}
