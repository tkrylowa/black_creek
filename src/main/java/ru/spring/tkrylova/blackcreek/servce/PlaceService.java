package ru.spring.tkrylova.blackcreek.servce;

import org.springframework.stereotype.Service;
import ru.spring.tkrylova.blackcreek.entity.Place;
import ru.spring.tkrylova.blackcreek.repository.PlacesRepository;

import java.util.List;

@Service
public class PlaceService {
    private final PlacesRepository placesRepository;

    public PlaceService(PlacesRepository placesRepository) {
        this.placesRepository = placesRepository;
    }

    public List<Place> getAllPlans() {
        return placesRepository.findAll();
    }

    public void savePlan(Place plan) {
        placesRepository.save(plan);
    }
}
