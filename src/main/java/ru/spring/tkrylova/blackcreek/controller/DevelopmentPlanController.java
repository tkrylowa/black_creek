package ru.spring.tkrylova.blackcreek.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.spring.tkrylova.blackcreek.entity.DevelopmentPlan;
import ru.spring.tkrylova.blackcreek.servce.DevelopmentPlanService;

import java.util.List;

@Controller
@RequestMapping("/development_plans")
public class DevelopmentPlanController {
    private final DevelopmentPlanService developmentPlanService;

    public DevelopmentPlanController(DevelopmentPlanService developmentPlanService) {
        this.developmentPlanService = developmentPlanService;
    }

    @GetMapping
    public String getAllDevelopmentPlans(Model model) {
        List<DevelopmentPlan> developmentPlans = developmentPlanService.getAllPlans();
        model.addAttribute("developmentPlans", developmentPlans);
        return "development_plans/development_plan";
    }

    @GetMapping("/new")
    public String showDevelopmentPlanForm(Model model) {
        model.addAttribute("developmentPlan", new DevelopmentPlan());
        return "development_plans/development_plan_form";
    }

    @PostMapping
    public String createDevelopmentPlan(@ModelAttribute DevelopmentPlan developmentPlan) {
        developmentPlanService.savePlan(developmentPlan);
        return "redirect:/development_plans/development_plans";
    }
}
