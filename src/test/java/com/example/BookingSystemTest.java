package com.example;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.Mock;
import org.mockito.InjectMocks;

import java.sql.Time;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookingSystemTest {

    @Mock
    TimeProvider timeProvider;
    RoomRepository roomRepository;
    NotificationService notificationService;

    @InjectMocks
    BookingSystem bookingSystem;

    @Test
    @DisplayName("When startTime is null throw exception")
    void bookRoomStartTimeIsNull() {
        LocalDateTime endTime = LocalDateTime.now().plusHours(1);
        assertThatThrownBy(() -> bookingSystem.bookRoom("1D", null, endTime))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Bokning kräver giltiga start- och sluttider samt rum-id");
    }

    @Test
    @DisplayName("When endTime time is null throw exception")
    void bookRoomEndTimeIsNull() {
        LocalDateTime starTime = LocalDateTime.now().plusHours(1);
        assertThatThrownBy(() -> bookingSystem.bookRoom("1D", starTime, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Bokning kräver giltiga start- och sluttider samt rum-id");
    }

    @Test
    @DisplayName("When roomID time is null throw exception")
    void bookRoomRoomIDIsNull() {
        LocalDateTime starTime = LocalDateTime.now().plusHours(1);
        LocalDateTime endTime = LocalDateTime.now().plusHours(2);
        assertThatThrownBy(() -> bookingSystem.bookRoom(null, starTime, endTime))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Bokning kräver giltiga start- och sluttider samt rum-id");
    }


}
