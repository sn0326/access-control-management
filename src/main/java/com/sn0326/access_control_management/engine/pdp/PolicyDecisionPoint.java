package com.sn0326.access_control_management.engine.pdp;

import com.sn0326.access_control_management.domain.policy.Policy;
import com.sn0326.access_control_management.domain.policy.PolicyCondition;
import com.sn0326.access_control_management.engine.pip.AccessContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * PDP（Policy Decision Point）
 *
 * <p>有効なポリシーをすべて評価し、{@link CombiningAlgorithm} に最終決定を委譲する。
 *
 * <p>結合アルゴリズムは {@link EngineConfig} で {@code pbac.combining-algorithm} の値に
 * 応じて Bean として注入される（DENY_OVERRIDES / PERMIT_OVERRIDES / FIRST_APPLICABLE）。
 */
@Service
@RequiredArgsConstructor
public class PolicyDecisionPoint {

    private final PolicyStore policyStore;
    private final TargetMatcher targetMatcher;
    private final ConditionEvaluator conditionEvaluator;
    private final CombiningAlgorithm combiningAlgorithm;

    public AccessDecision decide(AccessContext context) {
        List<Policy> policies = policyStore.findEnabledOrderByPriorityDesc();

        List<PolicyEvaluationResult> details = policies.stream()
                .map(policy -> evaluate(policy, context))
                .toList();

        return combiningAlgorithm.combine(details);
    }

    private PolicyEvaluationResult evaluate(Policy policy, AccessContext context) {
        boolean targetMatched = targetMatcher.matches(policy, context);
        boolean conditionsMet = targetMatched && evaluateAllConditions(policy.getConditions(), context);
        boolean applied       = targetMatched && conditionsMet;
        return new PolicyEvaluationResult(policy, targetMatched, conditionsMet, applied);
    }

    private boolean evaluateAllConditions(List<PolicyCondition> conditions, AccessContext context) {
        if (conditions.isEmpty()) {
            return true; // 条件なし = 無条件に成立
        }
        return conditions.stream().allMatch(c -> conditionEvaluator.evaluate(c, context));
    }
}
