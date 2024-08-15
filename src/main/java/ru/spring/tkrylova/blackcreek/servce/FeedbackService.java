package ru.spring.tkrylova.blackcreek.servce;

import org.springframework.stereotype.Service;
import ru.spring.tkrylova.blackcreek.entity.Feedback;
import ru.spring.tkrylova.blackcreek.repository.FeedbackRepository;

import java.util.List;

@Service
public class FeedbackService {
    private final FeedbackRepository feedbackRepository;

    public FeedbackService(FeedbackRepository feedbackRepository) {
        this.feedbackRepository = feedbackRepository;
    }

    public List<Feedback> getFeedbackByEventId(Long eventId) {
        if (eventId == null || eventId < 0) {
            throw new IllegalArgumentException("Feedback is null or illegal!");
        }
        return feedbackRepository.findByEventId(eventId);
    }

    public Feedback saveFeedback(Feedback feedback) {
        if (feedback == null) {
            throw new IllegalArgumentException("Feedback is null!");
        }
        return feedbackRepository.save(feedback);
    }
}
