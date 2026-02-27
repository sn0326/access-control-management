package com.sn0326.access_control_management.web.accesstest;

import com.sn0326.access_control_management.audit.AccessLog;
import com.sn0326.access_control_management.audit.AccessLogRepository;
import com.sn0326.access_control_management.domain.action.ActionRepository;
import com.sn0326.access_control_management.domain.resource.Resource;
import com.sn0326.access_control_management.domain.resource.ResourceRepository;
import com.sn0326.access_control_management.domain.user.User;
import com.sn0326.access_control_management.domain.user.UserRepository;
import com.sn0326.access_control_management.engine.pdp.AccessDecision;
import com.sn0326.access_control_management.engine.pdp.PolicyDecisionPoint;
import com.sn0326.access_control_management.engine.pip.AccessContext;
import com.sn0326.access_control_management.engine.pip.AttributeResolver;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/access-test")
@RequiredArgsConstructor
public class AccessTestController {

    private final UserRepository userRepository;
    private final ResourceRepository resourceRepository;
    private final ActionRepository actionRepository;
    private final AttributeResolver attributeResolver;
    private final PolicyDecisionPoint pdp;
    private final AccessLogRepository accessLogRepository;

    @GetMapping
    public String form(Model model) {
        model.addAttribute("users",     userRepository.findAll());
        model.addAttribute("resources", resourceRepository.findAll());
        model.addAttribute("actions",   actionRepository.findAll());
        return "access-test/index";
    }

    @PostMapping
    public String evaluate(
            @RequestParam Long userId,
            @RequestParam Long resourceId,
            @RequestParam String actionName,
            HttpServletRequest request,
            Model model) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
        Resource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new IllegalArgumentException("Resource not found: " + resourceId));

        // PIP: コンテキスト組み立て
        AccessContext context = attributeResolver.resolve(user, resource, actionName, request);

        // PDP: アクセス判定
        AccessDecision accessDecision = pdp.decide(context);

        // 監査ログ保存
        AccessLog log = new AccessLog();
        log.setUserId(userId);
        log.setResourceId(resourceId);
        log.setActionName(actionName);
        log.setDecision(accessDecision.decision().name());
        log.setMatchedPolicyId(accessDecision.matchedPolicyId());
        accessLogRepository.save(log);

        // View へ渡す
        model.addAttribute("users",          userRepository.findAll());
        model.addAttribute("resources",      resourceRepository.findAll());
        model.addAttribute("actions",        actionRepository.findAll());
        model.addAttribute("selectedUser",   user);
        model.addAttribute("selectedResource", resource);
        model.addAttribute("selectedAction", actionName);
        model.addAttribute("context",        context);
        model.addAttribute("accessDecision", accessDecision);

        return "access-test/index";
    }
}
