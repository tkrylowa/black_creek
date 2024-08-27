package ru.spring.tkrylova.blackcreek.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.spring.tkrylova.blackcreek.entity.DevelopmentPlan;
import ru.spring.tkrylova.blackcreek.servce.DevelopmentPlanService;

import java.util.List;

@Slf4j
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
        log.info("Found {} developments plans", developmentPlans.size());
        model.addAttribute("developmentPlans", developmentPlans);
        return "development_plans/development_plan";
    }

    @GetMapping("/add")
    public String showDevelopmentPlanForm(Model model) {
        model.addAttribute("developmentPlan", new DevelopmentPlan());
        return "development_plans/development_plan_form";
    }

    @PostMapping("/add")
    public String createDevelopmentPlan(@Valid DevelopmentPlan developmentPlan, BindingResult result, Model model) {
        model.addAttribute("developmentPlan", developmentPlan);
        if (result.hasErrors()) {
            log.error("Error occurred! {}", result.getAllErrors());
            model.addAttribute("error", result.getFieldErrors());
            return "development_plans/development_plan_form";
        }
        DevelopmentPlan newDevelopmentPlan = developmentPlanService.savePlan(developmentPlan);
        log.info("New development plan with id {} was successfully created", newDevelopmentPlan.getDevelopmentPlanId());
        return "redirect:/development_plans";
    }
}
