package ru.spring.tkrylova.blackcreek.servce;

import ru.spring.tkrylova.blackcreek.entity.DevelopmentPlan;
import ru.spring.tkrylova.blackcreek.entity.Place;
import ru.spring.tkrylova.blackcreek.repository.DevelopmentPlanRepository;
import ru.spring.tkrylova.blackcreek.repository.PlacesRepository;

import java.util.List;

public class PlaceService {
    private final PlacesRepository placesRepository;

    public PlaceService(PlacesRepository placesRepository) {
        this.placesRepository = placesRepository;
    }

    public List<Place> getAllPlans() {
        return placesRepository.findAll();
    }

    public Place savePlan(Place plan) {
        return placesRepository.save(plan);
    }
}
