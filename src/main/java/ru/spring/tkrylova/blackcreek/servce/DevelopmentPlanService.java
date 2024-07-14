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
        return developmentPlanRepository.findById(id).orElse(null);
    }

    public DevelopmentPlan savePlan(DevelopmentPlan plan) {
        return developmentPlanRepository.save(plan);
    }
}
