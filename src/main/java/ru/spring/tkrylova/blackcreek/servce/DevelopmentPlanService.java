package ru.spring.tkrylova.blackcreek.servce;

import org.springframework.stereotype.Service;
import ru.spring.tkrylova.blackcreek.entity.DevelopmentPlan;
import ru.spring.tkrylova.blackcreek.repository.DevelopmentPlanRepository;

import java.util.List;

@Service
public class DevelopmentPlanService {
    private final DevelopmentPlanRepository developmentPlanRepository;

    public DevelopmentPlanService(DevelopmentPlanRepository developmentPlanRepository) {
        this.developmentPlanRepository = developmentPlanRepository;
    }

    public List<DevelopmentPlan> getAllPlans() {
        return developmentPlanRepository.findAll();
    }

    public DevelopmentPlan getDevelopmentPlanById(Long id) {
        if (id == null || id < 0) {
            throw new IllegalArgumentException("Plan id is null or illegal!");
        }
        return developmentPlanRepository.findById(id).orElse(null);
    }

    public DevelopmentPlan savePlan(DevelopmentPlan plan) {
        if (plan == null) {
            throw new IllegalArgumentException("Feedback is null!");
        }
        return developmentPlanRepository.save(plan);
    }
}
