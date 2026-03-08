package com.sn0326.access_control_management.engine.pdp;

import java.util.List;

/**
 * ポリシー結合アルゴリズムのインターフェース。
 *
 * <p>全ポリシーの評価結果リストを受け取り、最終的な {@link AccessDecision} を返す。
 * 実装を差し替えることでアルゴリズムを変更できる。
 *
 * <p>標準実装は {@link EngineConfig} で Bean として登録される:
 * <ul>
 *   <li>DENY_OVERRIDES   - 1件でも DENY が適用されれば DENY</li>
 *   <li>PERMIT_OVERRIDES - 1件でも PERMIT が適用されれば PERMIT</li>
 *   <li>FIRST_APPLICABLE - 優先度順に最初に適用されたポリシーの Effect を採用</li>
 * </ul>
 */
@FunctionalInterface
public interface CombiningAlgorithm {

    /**
     * @param results 優先度降順で評価された全ポリシーの結果リスト
     * @return 最終アクセス決定
     */
    AccessDecision combine(List<PolicyEvaluationResult> results);
}
