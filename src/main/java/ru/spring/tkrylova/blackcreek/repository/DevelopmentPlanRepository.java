package ru.spring.tkrylova.blackcreek.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.spring.tkrylova.blackcreek.entity.DevelopmentPlan;

public interface DevelopmentPlanRepository extends JpaRepository<DevelopmentPlan, Long> {
}
