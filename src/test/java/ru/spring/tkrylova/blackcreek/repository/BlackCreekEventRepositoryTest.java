package ru.spring.tkrylova.blackcreek.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.spring.tkrylova.blackcreek.entity.BlackCreekEvent;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
@ActiveProfiles("test")
public class BlackCreekEventRepositoryTest {
    @Autowired
    private BlackCreekEventRepository blackCreekEventRepository;

    @BeforeEach
    public void setUp() {
        blackCreekEventRepository.deleteAllInBatch();
        BlackCreekEvent event1 = new BlackCreekEvent();
        event1.setEventName("Medieval Fair");
        event1.setEventStartDate(LocalDate.now().plusDays(2));
        event1.setEventEndDate(LocalDate.now().plusDays(3));
        event1.setEventDescription("A grand medieval fair event.");
        event1.setEventCapacity(100);
        blackCreekEventRepository.save(event1);

        BlackCreekEvent event2 = new BlackCreekEvent();
        event2.setEventName("Knight Tournament");
        event2.setEventStartDate(LocalDate.now().plusDays(5));
        event2.setEventEndDate(LocalDate.now().plusDays(6));
        event2.setEventDescription("A tournament featuring the bravest knights.");
        event2.setEventCapacity(50);
        blackCreekEventRepository.save(event2);

        BlackCreekEvent event3 = new BlackCreekEvent();
        event3.setEventName("Medieval Banquet");
        event3.setEventStartDate(LocalDate.now().plusDays(8));
        event3.setEventEndDate(LocalDate.now().plusDays(9));
        event3.setEventDescription("A royal banquet with medieval delicacies.");
        event3.setEventCapacity(75);
        blackCreekEventRepository.save(event3);
    }

    @Test
    public void findByEventStartDateBetween_Found() {
        LocalDate startDate = LocalDate.now().plusDays(1);
        LocalDate endDate = LocalDate.now().plusDays(6);
        List<BlackCreekEvent> events = blackCreekEventRepository.findByEventStartDateBetween(startDate, endDate);

        assertThat(events).hasSize(2);
        assertThat(events).extracting("eventName").containsExactlyInAnyOrder("Medieval Fair", "Knight Tournament");
    }

    @Test
    public void findByEventStartDateBetween_NotFound() {
        LocalDate startDate = LocalDate.now().minusDays(6);
        LocalDate endDate = LocalDate.now().minusDays(1);
        List<BlackCreekEvent> events = blackCreekEventRepository.findByEventStartDateBetween(startDate, endDate);

        assertThat(events).hasSize(0);
    }

    @Test
    public void testSearchEventByEventNameOrEventDescription_Found() {
        String keyword = "medieval";
        List<BlackCreekEvent> events = blackCreekEventRepository.searchEventByEventNameOrEventDescription(keyword);

        assertThat(events).hasSize(2);
        assertThat(events).extracting("eventName").containsExactlyInAnyOrder("Medieval Fair", "Medieval Banquet");

        keyword = "knight";
        events = blackCreekEventRepository.searchEventByEventNameOrEventDescription(keyword);

        assertThat(events).hasSize(1);
        assertThat(events).extracting("eventName").containsExactly("Knight Tournament");
    }

    @Test
    public void testSearchEventByEventNameOrEventDescription_NotFound() {
        String keyword = "not found";
        List<BlackCreekEvent> events = blackCreekEventRepository.searchEventByEventNameOrEventDescription(keyword);
        assertThat(events).hasSize(0);
    }
}
