package com.sn0326.access_control_management.engine.pdp;

import java.util.List;

/**
 * PDP が返す最終的なアクセス決定とその根拠。
 * /access-test UI での表示に使用する。
 */
public record AccessDecision(
        Decision decision,
        List<PolicyEvaluationResult> evaluationDetails,
        Long matchedPolicyId  // 判定の決め手になったポリシーID（任意）
) {
}
