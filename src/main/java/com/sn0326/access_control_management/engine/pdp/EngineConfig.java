package com.sn0326.access_control_management.engine.pdp;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * エンジン層の Bean 設定。
 *
 * <p>{@code pbac.combining-algorithm} の値に応じて {@link CombiningAlgorithm} の
 * 実装を選択して Bean として登録する。
 */
@Configuration
public class EngineConfig {

    @Value("${pbac.combining-algorithm:DENY_OVERRIDES}")
    private String algorithm;

    @Bean
    public CombiningAlgorithm combiningAlgorithm() {
        return switch (algorithm) {
            case "PERMIT_OVERRIDES" -> EngineConfig::permitOverrides;
            case "FIRST_APPLICABLE" -> EngineConfig::firstApplicable;
            default                 -> EngineConfig::denyOverrides;
        };
    }

    // --- 各アルゴリズムの実装 ---

    private static AccessDecision denyOverrides(List<PolicyEvaluationResult> results) {
        Long firstDenyId  = null;
        Long firstPermitId = null;
        boolean anyPermit  = false;

        for (PolicyEvaluationResult r : results) {
            if (!r.applied()) continue;
            if ("DENY".equals(r.policy().getEffect())) {
                if (firstDenyId == null) firstDenyId = r.policy().getId();
            } else {
                anyPermit = true;
                if (firstPermitId == null) firstPermitId = r.policy().getId();
            }
        }

        if (firstDenyId != null) return new AccessDecision(Decision.DENY,   results, firstDenyId);
        if (anyPermit)           return new AccessDecision(Decision.PERMIT,  results, firstPermitId);
        return                          new AccessDecision(Decision.NOT_APPLICABLE, results, null);
    }

    private static AccessDecision permitOverrides(List<PolicyEvaluationResult> results) {
        Long firstPermitId = null;
        Long firstDenyId   = null;
        boolean anyDeny    = false;

        for (PolicyEvaluationResult r : results) {
            if (!r.applied()) continue;
            if ("PERMIT".equals(r.policy().getEffect())) {
                if (firstPermitId == null) firstPermitId = r.policy().getId();
            } else {
                anyDeny = true;
                if (firstDenyId == null) firstDenyId = r.policy().getId();
            }
        }

        if (firstPermitId != null) return new AccessDecision(Decision.PERMIT, results, firstPermitId);
        if (anyDeny)               return new AccessDecision(Decision.DENY,   results, firstDenyId);
        return                            new AccessDecision(Decision.NOT_APPLICABLE, results, null);
    }

    private static AccessDecision firstApplicable(List<PolicyEvaluationResult> results) {
        for (PolicyEvaluationResult r : results) {
            if (!r.applied()) continue;
            Decision d = "DENY".equals(r.policy().getEffect()) ? Decision.DENY : Decision.PERMIT;
            return new AccessDecision(d, results, r.policy().getId());
        }
        return new AccessDecision(Decision.NOT_APPLICABLE, results, null);
    }
}
