package com.sn0326.access_control_management.engine.pdp;

import com.sn0326.access_control_management.domain.policy.Policy;
import com.sn0326.access_control_management.domain.policy.PolicyCondition;
import com.sn0326.access_control_management.domain.policy.PolicyRepository;
import com.sn0326.access_control_management.engine.pip.AccessContext;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * PDP（Policy Decision Point）
 *
 * <p>有効なポリシーを優先度順に評価し、結合アルゴリズムで最終決定を返す。
 *
 * <p>結合アルゴリズム（application.yaml の pbac.combining-algorithm で設定）:
 * <ul>
 *   <li>DENY_OVERRIDES    - 1件でも DENY が適用されれば DENY</li>
 *   <li>PERMIT_OVERRIDES  - 1件でも PERMIT が適用されれば PERMIT</li>
 *   <li>FIRST_APPLICABLE  - 優先度順に最初に適用されたポリシーの Effect を採用</li>
 * </ul>
 */
@Service
@RequiredArgsConstructor
public class PolicyDecisionPoint {

    private final PolicyRepository policyRepository;
    private final TargetMatcher targetMatcher;
    private final ConditionEvaluator conditionEvaluator;

    @Value("${pbac.combining-algorithm:DENY_OVERRIDES}")
    private String combiningAlgorithm;

    public AccessDecision decide(AccessContext context) {
        List<Policy> policies = policyRepository.findAllEnabledOrderByPriorityDesc();
        List<PolicyEvaluationResult> details = new ArrayList<>();

        boolean anyPermit = false;
        Long firstDenyPolicyId = null;
        Long firstPermitPolicyId = null;

        for (Policy policy : policies) {
            boolean targetMatched = targetMatcher.matches(policy, context);

            boolean conditionsMet = false;
            if (targetMatched) {
                conditionsMet = evaluateAllConditions(policy.getConditions(), context);
            }

            boolean applied = targetMatched && conditionsMet;
            details.add(new PolicyEvaluationResult(policy, targetMatched, conditionsMet, applied));

            if (!applied) continue;

            if ("DENY".equals(policy.getEffect())) {
                if ("DENY_OVERRIDES".equals(combiningAlgorithm)) {
                    // DENY_OVERRIDES: 即時 DENY を返す
                    details.add(new PolicyEvaluationResult(policy, targetMatched, conditionsMet, true));
                    return new AccessDecision(Decision.DENY, details, policy.getId());
                }
                if (firstDenyPolicyId == null) firstDenyPolicyId = policy.getId();
            }

            if ("PERMIT".equals(policy.getEffect())) {
                anyPermit = true;
                if (firstPermitPolicyId == null) firstPermitPolicyId = policy.getId();
                if ("FIRST_APPLICABLE".equals(combiningAlgorithm)) {
                    return new AccessDecision(Decision.PERMIT, details, firstPermitPolicyId);
                }
            }
        }

        return switch (combiningAlgorithm) {
            case "PERMIT_OVERRIDES" -> anyPermit
                    ? new AccessDecision(Decision.PERMIT, details, firstPermitPolicyId)
                    : new AccessDecision(firstDenyPolicyId != null ? Decision.DENY : Decision.NOT_APPLICABLE, details, firstDenyPolicyId);
            default -> // DENY_OVERRIDES (DENY がなければ PERMIT → NOT_APPLICABLE)
                    anyPermit
                    ? new AccessDecision(Decision.PERMIT, details, firstPermitPolicyId)
                    : new AccessDecision(Decision.NOT_APPLICABLE, details, null);
        };
    }

    private boolean evaluateAllConditions(List<PolicyCondition> conditions, AccessContext context) {
        if (conditions.isEmpty()) {
            return true; // 条件なし = 無条件に成立
        }
        return conditions.stream().allMatch(c -> conditionEvaluator.evaluate(c, context));
    }
}
