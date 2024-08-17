package ru.spring.tkrylova.blackcreek.service;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import ru.spring.tkrylova.blackcreek.entity.DevelopmentPlan;
import ru.spring.tkrylova.blackcreek.repository.DevelopmentPlanRepository;
import ru.spring.tkrylova.blackcreek.servce.DevelopmentPlanService;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class DevelopmentPlanServiceTests {

    @Mock
    private DevelopmentPlanRepository developmentPlanRepository;

    @InjectMocks
    private DevelopmentPlanService developmentPlanService;

    @Test
    void getAllPlans_ShouldReturnAllPlans() {
        DevelopmentPlan plan1 = new DevelopmentPlan(1L, "Plan 1", "Description 1", null, null, null, null, true);
        DevelopmentPlan plan2 = new DevelopmentPlan(2L, "Plan 2", "Description 2", null, null, null, null, true);

        when(developmentPlanRepository.findAll()).thenReturn(Arrays.asList(plan1, plan2));

        List<DevelopmentPlan> plans = developmentPlanService.getAllPlans();

        assertNotNull(plans, "The returned plans list should not be null");
        assertEquals(2, plans.size(), "The size of the plans list should be 2");
        assertEquals(plan1, plans.get(0), "The first plan should match");
        assertEquals(plan2, plans.get(1), "The second plan should match");

        verify(developmentPlanRepository, times(1)).findAll();
    }

    @Test
    void getDevelopmentPlanById_ShouldReturnPlan_WhenIdIsValid() {
        Long planId = 1L;
        DevelopmentPlan plan = new DevelopmentPlan(planId, "Plan 1", "Description 1", null, null, null, null, true);

        when(developmentPlanRepository.findById(planId)).thenReturn(Optional.of(plan));

        DevelopmentPlan result = developmentPlanService.getDevelopmentPlanById(planId);

        assertNotNull(result, "The returned plan should not be null");
        assertEquals(plan, result, "The returned plan should match the expected plan");

        verify(developmentPlanRepository, times(1)).findById(planId);
    }

    @Test
    void getDevelopmentPlanById_ShouldReturnNull_WhenPlanDoesNotExist() {
        Long planId = 1L;
        when(developmentPlanRepository.findById(planId)).thenReturn(Optional.empty());

        DevelopmentPlan result = developmentPlanService.getDevelopmentPlanById(planId);

        assertNull(result, "The returned plan should be null when it does not exist");

        verify(developmentPlanRepository, times(1)).findById(planId);
    }

    @Test
    void getDevelopmentPlanById_ShouldThrowException_WhenIdIsNull() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                developmentPlanService.getDevelopmentPlanById(null)
        );
        assertEquals("Plan id is null or illegal!", exception.getMessage());

        verify(developmentPlanRepository, never()).findById(anyLong());
    }

    @Test
    void getDevelopmentPlanById_ShouldThrowException_WhenIdIsNegative() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                developmentPlanService.getDevelopmentPlanById(-1L)
        );
        assertEquals("Plan id is null or illegal!", exception.getMessage());

        verify(developmentPlanRepository, never()).findById(-1L);
    }

    @Test
    void savePlan_ShouldSavePlan_WhenPlanIsValid() {
        DevelopmentPlan plan = new DevelopmentPlan(1L, "Plan 1", "Description 1", null, null, null, null, true);
        when(developmentPlanRepository.save(plan)).thenReturn(plan);

        DevelopmentPlan savedPlan = developmentPlanService.savePlan(plan);

        assertNotNull(savedPlan, "The saved plan should not be null");
        assertEquals(plan, savedPlan, "The saved plan should match the original plan");

        verify(developmentPlanRepository, times(1)).save(plan);
    }

    @Test
    void savePlan_ShouldThrowException_WhenPlanIsNull() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                developmentPlanService.savePlan(null)
        );
        assertEquals("Feedback is null!", exception.getMessage());

        verify(developmentPlanRepository, never()).save(any());
    }
}
