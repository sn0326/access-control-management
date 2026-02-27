package com.sn0326.access_control_management.web.policy;

import com.sn0326.access_control_management.domain.policy.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/policies")
@RequiredArgsConstructor
public class PolicyController {

    private final PolicyRepository policyRepository;
    private final PolicyTargetRepository policyTargetRepository;
    private final PolicyConditionRepository policyConditionRepository;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("policies", policyRepository.findAll());
        return "policies/list";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("policy", new Policy());
        return "policies/form";
    }

    @PostMapping
    public String create(@ModelAttribute Policy policy) {
        Policy saved = policyRepository.save(policy);
        return "redirect:/policies/" + saved.getId();
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        Policy policy = policyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Policy not found: " + id));
        model.addAttribute("policy", policy);
        model.addAttribute("newTarget", new PolicyTarget());
        model.addAttribute("newCondition", new PolicyCondition());
        return "policies/detail";
    }

    @PostMapping("/{id}/targets")
    public String addTarget(@PathVariable Long id, @ModelAttribute PolicyTarget target) {
        Policy policy = policyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Policy not found: " + id));
        target.setPolicy(policy);
        policyTargetRepository.save(target);
        return "redirect:/policies/" + id;
    }

    @GetMapping("/{id}/targets/{targetId}/delete")
    public String deleteTarget(@PathVariable Long id, @PathVariable Long targetId) {
        policyTargetRepository.deleteById(targetId);
        return "redirect:/policies/" + id;
    }

    @PostMapping("/{id}/conditions")
    public String addCondition(@PathVariable Long id, @ModelAttribute PolicyCondition condition) {
        Policy policy = policyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Policy not found: " + id));
        condition.setPolicy(policy);
        policyConditionRepository.save(condition);
        return "redirect:/policies/" + id;
    }

    @GetMapping("/{id}/conditions/{conditionId}/delete")
    public String deleteCondition(@PathVariable Long id, @PathVariable Long conditionId) {
        policyConditionRepository.deleteById(conditionId);
        return "redirect:/policies/" + id;
    }

    @GetMapping("/{id}/toggle")
    public String toggle(@PathVariable Long id) {
        policyRepository.findById(id).ifPresent(p -> {
            p.setEnabled(!p.isEnabled());
            policyRepository.save(p);
        });
        return "redirect:/policies";
    }

    @GetMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        policyRepository.deleteById(id);
        return "redirect:/policies";
    }
}
