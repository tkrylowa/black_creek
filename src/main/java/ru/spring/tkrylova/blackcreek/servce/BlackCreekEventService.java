package ru.spring.tkrylova.blackcreek.servce;

import org.springframework.stereotype.Service;
import ru.spring.tkrylova.blackcreek.entity.BlackCreekEvent;
import ru.spring.tkrylova.blackcreek.repository.BlackCreekEventRepository;

import java.util.List;

@Service
public class BlackCreekEventService {
    private final BlackCreekEventRepository blackCreekEventRepository;

    public BlackCreekEventService(BlackCreekEventRepository blackCreekEventRepository) {
        this.blackCreekEventRepository = blackCreekEventRepository;
    }

    public List<BlackCreekEvent> getAllEvents() {
        return blackCreekEventRepository.findAll();
    }

    public BlackCreekEvent getEventById(Long id) {
        return blackCreekEventRepository.findById(id).orElse(null);
    }

    public void saveEvent(BlackCreekEvent event) {
        blackCreekEventRepository.save(event);
    }
}
