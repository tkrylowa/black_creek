package ru.spring.tkrylova.blackcreek.servce;

import org.springframework.stereotype.Service;
import ru.spring.tkrylova.blackcreek.entity.EventPhoto;
import ru.spring.tkrylova.blackcreek.repository.EventPhotoRepository;

@Service
public class EventPhotoService {
    private final EventPhotoRepository eventPhotoRepository;

    public EventPhotoService(EventPhotoRepository eventPhotoRepository) {
        this.eventPhotoRepository = eventPhotoRepository;
    }

    public EventPhoto save(EventPhoto eventPhoto) {
        return eventPhotoRepository.save(eventPhoto);
    }
}
