package ru.spring.tkrylova.blackcreek.service;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import ru.spring.tkrylova.blackcreek.entity.Place;
import ru.spring.tkrylova.blackcreek.repository.PlacesRepository;
import ru.spring.tkrylova.blackcreek.servce.PlaceService;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class PlaceServiceTest {
    @Mock
    private PlacesRepository placesRepository;

    @InjectMocks
    private PlaceService placeService;

    @Test
    void getAllPlans_ReturnsListOfPlaces() {
        List<Place> places = new ArrayList<>();
        Place place1 = new Place();
        place1.setPlaceId(1L);
        place1.setPlaceName("Place 1");
        places.add(place1);

        Place place2 = new Place();
        place2.setPlaceId(2L);
        place2.setPlaceName("Place 2");
        places.add(place2);

        when(placesRepository.findAll()).thenReturn(places);

        List<Place> result = placeService.getAllPlans();

        assertNotNull(result, "The result should not be null");
        assertEquals(2, result.size(), "The result should contain 2 places");
        assertEquals("Place 1", result.get(0).getPlaceName(),
                "The first place should be 'Place 1'");
        assertEquals("Place 2", result.get(1).getPlaceName(),
                "The second place should be 'Place 2'");

        verify(placesRepository, times(1)).findAll();
    }

    @Test
    void savePlan_Success() {
        Place place = new Place();
        place.setPlaceId(1L);
        place.setPlaceName("New Place");

        placeService.savePlan(place);
        verify(placesRepository, times(1)).save(place);
    }

    @Test
    void savePlan__WithNullPlace_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> placeService.savePlan(null),
                "Saving a null Place should throw an IllegalArgumentException");

        verify(placesRepository, times(0)).save(any(Place.class));
    }
}
