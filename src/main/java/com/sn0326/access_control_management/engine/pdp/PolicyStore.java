package com.sn0326.access_control_management.engine.pdp;

import com.sn0326.access_control_management.domain.policy.Policy;

import java.util.List;

/**
 * PDP がポリシーを取得するためのインターフェース。
 *
 * <p>JPA への直接依存を切り離し、テスト時やインメモリ実装への差し替えを可能にする。
 */
public interface PolicyStore {

    /** 有効なポリシーを優先度の降順で返す。 */
    List<Policy> findEnabledOrderByPriorityDesc();
}
