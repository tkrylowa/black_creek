package ru.spring.tkrylova.blackcreek.entity;

import jakarta.persistence.Table;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class EventPhotoTest {
    private EventPhoto eventPhoto;
    private BlackCreekEvent event;

    @BeforeEach
    void setUp() {
        event = new BlackCreekEvent();
        event.setEventId(1L);

        eventPhoto = new EventPhoto();
        eventPhoto.setId(1L);
        eventPhoto.setPhotoUrl("http://example.com/photo.jpg");
        eventPhoto.setEvent(event);
    }

    @Test
    void eventPhoto_DefaultConstructor() {
        EventPhoto defaultEventPhoto = new EventPhoto();
        assertThat(defaultEventPhoto).isNotNull();
        assertThat(defaultEventPhoto.getId()).isNull();
        assertThat(defaultEventPhoto.getPhotoUrl()).isNull();
        assertThat(defaultEventPhoto.getEvent()).isNull();
    }

    @Test
    void eventPhoto_ParameterizedConstructor() {
        EventPhoto parameterizedEventPhoto = new EventPhoto(1L, "http://example.com/photo.jpg", event);
        assertThat(parameterizedEventPhoto.getId()).isEqualTo(1L);
        assertThat(parameterizedEventPhoto.getPhotoUrl()).isEqualTo("http://example.com/photo.jpg");
        assertThat(parameterizedEventPhoto.getEvent()).isEqualTo(event);
    }

    @Test
    void eventPhoto_GettersAndSetters() {
        eventPhoto.setId(2L);
        eventPhoto.setPhotoUrl("http://example.com/newphoto.jpg");
        eventPhoto.setEvent(null);

        assertThat(eventPhoto.getId()).isEqualTo(2L);
        assertThat(eventPhoto.getPhotoUrl()).isEqualTo("http://example.com/newphoto.jpg");
        assertThat(eventPhoto.getEvent()).isNull();
    }

    @Test
    void eventPhoto_ToString() {
        assertThat(eventPhoto.toString()).contains("id=1")
                .contains("photoUrl=http://example.com/photo.jpg");
    }

    @Test
    void eventPhoto_tableName() {
        Class<EventPhoto> entityClass = EventPhoto.class;
        assertTrue(entityClass.isAnnotationPresent(Table.class), "EventPhoto class should be annotated with @Table");
        Table tableAnnotation = entityClass.getAnnotation(Table.class);
        assertEquals("photo", tableAnnotation.name(), "Table name should be 'photo'");
    }
}
