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
        return feedbackRepository.findByEventId(eventId);
    }

    public void saveFeedback(Feedback feedback) {
        feedbackRepository.save(feedback);
    }
}
