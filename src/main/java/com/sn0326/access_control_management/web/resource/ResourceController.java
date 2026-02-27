package com.sn0326.access_control_management.web.resource;

import com.sn0326.access_control_management.domain.resource.Resource;
import com.sn0326.access_control_management.domain.resource.ResourceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/resources")
@RequiredArgsConstructor
public class ResourceController {

    private final ResourceRepository resourceRepository;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("resources", resourceRepository.findAll());
        return "resources/list";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("resource", new Resource());
        return "resources/form";
    }

    @PostMapping
    public String create(@ModelAttribute Resource resource) {
        resourceRepository.save(resource);
        return "redirect:/resources";
    }

    @GetMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        resourceRepository.deleteById(id);
        return "redirect:/resources";
    }
}
