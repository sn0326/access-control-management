package com.sn0326.access_control_management.engine.pdp;

import com.sn0326.access_control_management.domain.policy.Policy;

/**
 * 個々のポリシーに対する評価結果。
 * /access-test UI でのデバッグ表示に使用する。
 */
public record PolicyEvaluationResult(
        Policy policy,
        boolean targetMatched,
        boolean conditionsMet,
        boolean applied        // targetMatched && conditionsMet
) {
}
