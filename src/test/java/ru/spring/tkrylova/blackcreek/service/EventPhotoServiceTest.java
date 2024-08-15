package ru.spring.tkrylova.blackcreek.service;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import ru.spring.tkrylova.blackcreek.entity.EventPhoto;
import ru.spring.tkrylova.blackcreek.repository.EventPhotoRepository;
import ru.spring.tkrylova.blackcreek.servce.EventPhotoService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class EventPhotoServiceTest {
    @Mock
    private EventPhotoRepository eventPhotoRepository;

    @InjectMocks
    private EventPhotoService eventPhotoService;

    @Test
    void saveEventPhoto_Success() {
        EventPhoto eventPhoto = new EventPhoto();
        eventPhoto.setId(1L);
        eventPhoto.setPhotoUrl("http://example.com/photo.jpg");
        when(eventPhotoRepository.save(eventPhoto)).thenReturn(eventPhoto);
        EventPhoto savedPhoto = eventPhotoService.save(eventPhoto);
        assertNotNull(savedPhoto, "The saved photo should not be null");
        assertEquals(eventPhoto.getId(), savedPhoto.getId(), "The saved photo ID should match the original");
        assertEquals(eventPhoto.getPhotoUrl(), savedPhoto.getPhotoUrl(), "The saved photo URL should match the original");
        verify(eventPhotoRepository, times(1)).save(eventPhoto);
    }

    @Test
    void saveEventPhoto_WithNullEventPhoto_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () ->
                        eventPhotoService.save(null),
                "Saving a null EventPhoto should throw an IllegalArgumentException");
        verify(eventPhotoRepository, times(0)).save(any(EventPhoto.class));
    }
}
