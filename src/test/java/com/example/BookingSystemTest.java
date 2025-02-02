package com.example;


import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;


import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class BookingSystemTest {


    private static final int ONE_HOUR = 1;
    private static final String ROOM_ID = "1D";
    private static final String BOOKING_ID = "booking1";


    private LocalDateTime startTime;
    private LocalDateTime endTime;


    @Mock
    private TimeProvider timeProvider;


    @Mock
    private RoomRepository roomRepository;


    @Mock
    private NotificationService notificationService;


    @InjectMocks
    private BookingSystem bookingSystem;


    @BeforeEach
    void setUp() {
        startTime = LocalDateTime.now();
        endTime = startTime.plusHours(ONE_HOUR);
    }


    @Nested
    @DisplayName("bookRoom Tests")
    class BookRoomTests {


        @Test
        @DisplayName("Throws exception when startTime is null")
        void whenStartTimeIsNullThenThrowException() {
            assertThatThrownBy(() -> bookingSystem.bookRoom(ROOM_ID, null, endTime))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Bokning kräver giltiga start- och sluttider samt rum-id");
        }


        @Test
        @DisplayName("Throws exception when endTime is null")
        void whenEndTimeIsNullThenThrowException() {
            assertThatThrownBy(() -> bookingSystem.bookRoom(ROOM_ID, startTime, null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Bokning kräver giltiga start- och sluttider samt rum-id");
        }


        @Test
        @DisplayName("Throws exception when roomId is null")
        void whenRoomIdIsNullThenThrowException() {
            assertThatThrownBy(() -> bookingSystem.bookRoom(null, startTime, endTime))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Bokning kräver giltiga start- och sluttider samt rum-id");
        }


        @Test
        @DisplayName("Throws exception when startTime is before current time")
        void whenStartTimeBeforeCurrentTimeThenThrowException() {
            when(timeProvider.getCurrentTime()).thenReturn(startTime);
            assertThatThrownBy(() -> bookingSystem.bookRoom(ROOM_ID, startTime.minusHours(ONE_HOUR), endTime))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Kan inte boka tid i dåtid");
        }


        @Test
        @DisplayName("Throws exception when endTime is before startTime")
        void whenEndTimeBeforeStartTimeThenThrowException() {
            when(timeProvider.getCurrentTime()).thenReturn(startTime);
            assertThatThrownBy(() -> bookingSystem.bookRoom(ROOM_ID, startTime, endTime.minusHours(2)))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Sluttid måste vara efter starttid");
        }


        @Test
        @DisplayName("Throws exception when roomId is not found")
        void whenRoomIdNotFoundThenThrowException() {
            when(roomRepository.findById(ROOM_ID)).thenReturn(Optional.empty());
            when(timeProvider.getCurrentTime()).thenReturn(startTime);
            assertThatThrownBy(() -> bookingSystem.bookRoom(ROOM_ID, startTime, endTime))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Rummet existerar inte");
        }


        @Test
        @DisplayName("Returns false when room is not available")
        void whenRoomIsNotAvailableThenReturnFalse() {
            Room room = mock(Room.class);
            when(timeProvider.getCurrentTime()).thenReturn(startTime);
            when(roomRepository.findById(ROOM_ID)).thenReturn(Optional.of(room));
            when(room.isAvailable(startTime, endTime)).thenReturn(false);
            assertThat(bookingSystem.bookRoom(ROOM_ID, startTime, endTime)).isFalse();
        }


        @Test
        @DisplayName("Returns true when room is available")
        void whenRoomIsAvailableThenReturnTrue() {
            Room room = mock(Room.class);
            when(timeProvider.getCurrentTime()).thenReturn(startTime);
            when(roomRepository.findById(ROOM_ID)).thenReturn(Optional.of(room));
            when(room.isAvailable(startTime, endTime)).thenReturn(true);
            assertThat(bookingSystem.bookRoom(ROOM_ID, startTime, endTime)).isTrue();
        }
    }


    @Nested
    @DisplayName("getAvailableRooms Tests")
    class GetAvailableRoomsTests {


        @Test
        @DisplayName("Throws exception when startTime is null")
        void whenStartTimeIsNullThenThrowException() {
            assertThatThrownBy(() -> bookingSystem.getAvailableRooms(null, endTime))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Måste ange både start- och sluttid");
        }


        @Test
        @DisplayName("Throws exception when endTime is null")
        void whenEndTimeIsNullThenThrowException() {
            assertThatThrownBy(() -> bookingSystem.getAvailableRooms(startTime, null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Måste ange både start- och sluttid");
        }


        @Test
        @DisplayName("Throws exception when startTime is after endTime")
        void whenStartTimeAfterEndTimeThenThrowException() {
            assertThatThrownBy(() -> bookingSystem.getAvailableRooms(endTime, startTime))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Sluttid måste vara efter starttid");
        }


        @Test
        @DisplayName("Returns available rooms")
        void whenRoomsAreAvailableThenReturnList() {
            Room room = mock(Room.class);
            when(roomRepository.findAll()).thenReturn(Collections.singletonList(room));
            when(room.isAvailable(startTime, endTime)).thenReturn(true);
            List<Room> availableRooms = bookingSystem.getAvailableRooms(startTime, endTime);
            assertThat(availableRooms).hasSize(1).contains(room);
        }
    }


    @Nested
    @DisplayName("cancelBooking Tests")
    class CancelBookingTests {


        @Test
        @DisplayName("Throws exception when bookingId is null")
        void whenBookingIdIsNullThenThrowException() {
            assertThatThrownBy(() -> bookingSystem.cancelBooking(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Boknings-id kan inte vara null");
        }


        @Test
        @DisplayName("Returns false when booking does not exist")
        void whenBookingDoesNotExistThenReturnFalse() {
            when(roomRepository.findAll()).thenReturn(Collections.emptyList());
            assertThat(bookingSystem.cancelBooking(BOOKING_ID)).isFalse();
        }


        @Test
        @DisplayName("Throws exception when booking is in the past")
        void whenBookingIsInPastThenThrowException() {
            Room room = mock(Room.class);
            Booking booking = new Booking(BOOKING_ID, ROOM_ID, startTime.minusHours(ONE_HOUR), endTime.minusHours(ONE_HOUR));
            when(roomRepository.findAll()).thenReturn(Collections.singletonList(room));
            when(room.hasBooking(BOOKING_ID)).thenReturn(true);
            when(room.getBooking(BOOKING_ID)).thenReturn(booking);
            when(timeProvider.getCurrentTime()).thenReturn(startTime);
            assertThatThrownBy(() -> bookingSystem.cancelBooking(BOOKING_ID))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessage("Kan inte avboka påbörjad eller avslutad bokning");
        }


        @Test
        @DisplayName("Returns true when booking is cancelled")
        void whenBookingIsCancelledThenReturnTrue() throws NotificationException {
            Room room = mock(Room.class);
            Booking booking = new Booking(BOOKING_ID, ROOM_ID, startTime, endTime);
            when(roomRepository.findAll()).thenReturn(Collections.singletonList(room));
            when(room.hasBooking(BOOKING_ID)).thenReturn(true);
            when(room.getBooking(BOOKING_ID)).thenReturn(booking);
            when(timeProvider.getCurrentTime()).thenReturn(startTime);
            assertThat(bookingSystem.cancelBooking(BOOKING_ID)).isTrue();
            verify(roomRepository, times(1)).save(room);
            verify(notificationService, times(1)).sendCancellationConfirmation(booking);
        }


        @Test
        @DisplayName("Continues when notification fails")
        void whenNotificationFailsThenContinue() throws NotificationException {
            Room room = mock(Room.class);
            Booking booking = new Booking(BOOKING_ID, ROOM_ID, startTime, endTime);
            when(roomRepository.findAll()).thenReturn(Collections.singletonList(room));
            when(room.hasBooking(BOOKING_ID)).thenReturn(true);
            when(room.getBooking(BOOKING_ID)).thenReturn(booking);
            when(timeProvider.getCurrentTime()).thenReturn(startTime);
            doThrow(new NotificationException("Failed to send notification")).when(notificationService).sendCancellationConfirmation(booking);
            assertThat(bookingSystem.cancelBooking(BOOKING_ID)).isTrue();
            verify(roomRepository, times(1)).save(room);
        }
    }
}

