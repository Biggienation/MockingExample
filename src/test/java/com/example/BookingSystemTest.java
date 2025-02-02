package com.example;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.Mock;
import org.mockito.InjectMocks;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookingSystemTest {

    int ONE_HOUR = 1;
    LocalDateTime startTime = LocalDateTime.now();
    LocalDateTime endTime = LocalDateTime.now();

    @Mock
    TimeProvider timeProvider;
    @Mock
    RoomRepository roomRepository;
    @Mock
    NotificationService notificationService;

    @InjectMocks
    BookingSystem bookingSystem;

    @Test
    @DisplayName("When startTime is null throw exception")
    void bookRoomStartTimeIsNull() {
        assertThatThrownBy(() -> bookingSystem.bookRoom("1D", null, endTime))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Bokning kräver giltiga start- och sluttider samt rum-id");
    }

    @Test
    @DisplayName("When endTime time is null throw exception")
    void bookRoomEndTimeIsNull() {
        assertThatThrownBy(() -> bookingSystem.bookRoom("1D", startTime, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Bokning kräver giltiga start- och sluttider samt rum-id");
    }

    @Test
    @DisplayName("When roomID time is null throw exception")
    void bookRoomRoomIDIsNull() {
        assertThatThrownBy(() -> bookingSystem.bookRoom(null, startTime, endTime))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Bokning kräver giltiga start- och sluttider samt rum-id");
    }

    @Test
    @DisplayName("When startTime is before current time throw exception")
    void bookRoomStartTimeBeforeCurrentTime() {
        when(timeProvider.getCurrentTime()).thenReturn(LocalDateTime.now());
        assertThatThrownBy(() -> bookingSystem.bookRoom("1D",
                startTime.minusHours(ONE_HOUR), endTime.plusHours(ONE_HOUR)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Kan inte boka tid i dåtid");
    }

    @Test
    @DisplayName("When endTime is before current time throw exception")
    void bookRoomEndTimeBeforeCurrentTime() {
        when(timeProvider.getCurrentTime()).thenReturn(LocalDateTime.now());
        assertThatThrownBy(() -> bookingSystem.bookRoom("1D",
                startTime.plusHours(ONE_HOUR), endTime.minusHours(ONE_HOUR)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Sluttid måste vara efter starttid");
    }

    @Test
    @DisplayName("Throws exception when roomRepository cant find roomId")
    void cantFindRoomId() {
        when(roomRepository.findById("1D")).thenReturn(Optional.empty());
        assertThatThrownBy(() -> bookingSystem.bookRoom("1D",
                startTime.plusHours(ONE_HOUR), endTime.plusHours(ONE_HOUR)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Rummet existerar inte");
    }


    //_____
    @Test
    void getAvailableRooms_ShouldThrowException_WhenStartTimeIsNull() {
        LocalDateTime endTime = LocalDateTime.now().plusHours(1);
        assertThatThrownBy(() -> bookingSystem.getAvailableRooms(null, endTime))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Måste ange både start- och sluttid");
    }

    @Test
    void getAvailableRooms_ShouldThrowException_WhenEndTimeIsNull() {
        LocalDateTime startTime = LocalDateTime.now().plusHours(1);
        assertThatThrownBy(() -> bookingSystem.getAvailableRooms(startTime, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Måste ange både start- och sluttid");
    }

    @Test
    void getAvailableRooms_ShouldThrowException_WhenEndTimeIsBeforeStartTime() {
        LocalDateTime startTime = LocalDateTime.now().plusHours(1);
        LocalDateTime endTime = startTime.minusHours(1);
        assertThatThrownBy(() -> bookingSystem.getAvailableRooms(startTime, endTime))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Sluttid måste vara efter starttid");
    }

    @Test
    void getAvailableRooms_ShouldReturnAvailableRooms() {
        LocalDateTime startTime = LocalDateTime.now().plusHours(1);
        LocalDateTime endTime = startTime.plusHours(1);
        Room room = Mockito.mock(Room.class);
        when(roomRepository.findAll()).thenReturn(Collections.singletonList(room));
        when(room.isAvailable(startTime, endTime)).thenReturn(true);
        List<Room> availableRooms = bookingSystem.getAvailableRooms(startTime, endTime);
        assertThat(availableRooms).hasSize(1).contains(room);
    }
    @Test
    void cancelBooking_ShouldThrowException_WhenBookingIdIsNull() {
        assertThatThrownBy(() -> bookingSystem.cancelBooking(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Boknings-id kan inte vara null");
    }

    @Test
    void cancelBooking_ShouldReturnFalse_WhenBookingDoesNotExist() {
        when(roomRepository.findAll()).thenReturn(Collections.emptyList());
        assertThat(bookingSystem.cancelBooking("booking1")).isFalse();
    }

    @Test
    void cancelBooking_ShouldThrowException_WhenBookingIsInPast() {
        LocalDateTime startTime = LocalDateTime.now().minusHours(1);
        LocalDateTime endTime = startTime.plusHours(1);
        Room room = Mockito.mock(Room.class);
        Booking booking = new Booking("booking1", "room1", startTime, endTime);
        when(roomRepository.findAll()).thenReturn(Collections.singletonList(room));
        when(room.hasBooking("booking1")).thenReturn(true);
        when(room.getBooking("booking1")).thenReturn(booking);
        when(timeProvider.getCurrentTime()).thenReturn(LocalDateTime.now());
        assertThatThrownBy(() -> bookingSystem.cancelBooking("booking1"))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Kan inte avboka påbörjad eller avslutad bokning");
    }

    @Test
    void cancelBooking_ShouldReturnTrue_WhenBookingIsCancelled() throws NotificationException {
        LocalDateTime startTime = LocalDateTime.now().plusHours(1);
        LocalDateTime endTime = startTime.plusHours(1);
        Room room = Mockito.mock(Room.class);
        Booking booking = new Booking("booking1", "room1", startTime, endTime);
        when(roomRepository.findAll()).thenReturn(Collections.singletonList(room));
        when(room.hasBooking("booking1")).thenReturn(true);
        when(room.getBooking("booking1")).thenReturn(booking);
        when(timeProvider.getCurrentTime()).thenReturn(LocalDateTime.now());
        assertThat(bookingSystem.cancelBooking("booking1")).isTrue();
        verify(roomRepository, times(1)).save(room);
        verify(notificationService, times(1)).sendCancellationConfirmation(booking);
    }

    @Test
    void cancelBooking_ShouldContinue_WhenNotificationFails() throws NotificationException {
        LocalDateTime startTime = LocalDateTime.now().plusHours(1);
        LocalDateTime endTime = startTime.plusHours(1);
        Room room = Mockito.mock(Room.class);
        Booking booking = new Booking("booking1", "room1", startTime, endTime);
        when(roomRepository.findAll()).thenReturn(Collections.singletonList(room));
        when(room.hasBooking("booking1")).thenReturn(true);
        when(room.getBooking("booking1")).thenReturn(booking);
        when(timeProvider.getCurrentTime()).thenReturn(LocalDateTime.now());
        doThrow(new NotificationException("Failed to send notification")).when(notificationService).sendCancellationConfirmation(booking);
        assertThat(bookingSystem.cancelBooking("booking1")).isTrue();
        verify(roomRepository, times(1)).save(room);
    }
}
