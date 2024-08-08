package ru.spring.tkrylova.blackcreek.servce;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.spring.tkrylova.blackcreek.entity.BlackCreekEvent;
import ru.spring.tkrylova.blackcreek.entity.BlackCreekUser;

import java.time.LocalDate;
import java.util.List;

@Component
public class EventNotificationScheduler {
    @Autowired
    private BlackCreekEventService blackCreekEventService;

    @Autowired
    private BlackCreekUserService blackCreekUserService;

    @Autowired
    private EmailService emailService;

    @Scheduled(cron = "0 0 8 * * *") // Runs every day at 8 AM
    public void sendUpcomingEventNotifications() {
        LocalDate now = LocalDate.now();
        LocalDate upcoming = now.plusDays(1); // Notify about events happening within the next day
        List<BlackCreekEvent> upcomingEvents = blackCreekEventService.findEventsBetween(now, upcoming);
        for (BlackCreekEvent event : upcomingEvents) {
            notifyUsersOfUpcomingEvent(event);
        }
    }

    public void notifyUsersOfUpcomingEvent(BlackCreekEvent event) {
        if (event == null) {
            throw new IllegalArgumentException("Event must not be null");
        }
        String subjectEvent = "Upcoming Event: " + event.getEventStartDate();
        for (BlackCreekUser user : event.getUsers()) {
            String body = "Dear " + user.getLogin() + ",\n\nThis is a reminder for the upcoming event \"" + event.getEventName() + "\". It will start on " +
                    event.getEventStartDate() + " and end on " + event.getEventEndDate() + ".\n\nBest regards,\nYour Medieval Park Team";
            emailService.sendEmail(user.getEmail(), subjectEvent, body);
        }

        if (event.getResponsibleUserId() != null) {
            String subject = "You are Responsible for an Upcoming Event: " + event.getEventName();
            BlackCreekUser user = blackCreekUserService.findUserById(event.getResponsibleUserId());
            String body = "Dear " + user.getLogin() + ",\n\nThis is a reminder that you are responsible for the upcoming event \"" + event.getEventName() + "\". It will start on " +
                    event.getEventStartDate() + " and end on " + event.getEventEndDate() + ".\n\nBest regards,\nYour Medieval Park Team";
            emailService.sendEmail(user.getEmail(), subject, body);
        }
    }
}
