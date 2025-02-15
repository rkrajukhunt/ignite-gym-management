/**
 * Author: Raju Khunt
 * Created by: rajukhunt on Date:15/02/25
 */

package com.ignite.gymmanagement.service;

import com.ignite.gymmanagement.dto.ClassBookingRequestDto;
import com.ignite.gymmanagement.dto.ClassBookingResponseDto;
import com.ignite.gymmanagement.dto.GymClassRequestDto;
import com.ignite.gymmanagement.dto.GymClassResponseDto;
import com.ignite.gymmanagement.model.ClassBooking;
import com.ignite.gymmanagement.model.GymClass;
import com.ignite.gymmanagement.repository.BookingRepository;
import com.ignite.gymmanagement.repository.GymClassRepository;
import com.ignite.gymmanagement.util.GenericResponse;
import com.ignite.gymmanagement.util.ResponseConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GymServiceTest {

    @Mock
    private GymClassRepository gymClassRepository;

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private GymService gymService;

    private LocalDate startDate;
    private LocalDate endDate;
    private GymClass gymClass;
    private ClassBookingRequestDto bookingRequest;
    private GymClassRequestDto gymClassRequest;

    /**
     * Initializes test data before each test execution.
     */
    @BeforeEach
    void setUp() {
        Clock clock = Clock.systemDefaultZone(); // Use system time
        LocalDate today = LocalDate.now(clock); // Get today's date dynamically

        startDate = today.plusDays(1); // Start date is tomorrow
        endDate = today.plusMonths(1); // End date is one month from today

        gymClass = GymClass.builder()
                .id(1L)
                .name("Yoga Class")
                .startDate(startDate)
                .endDate(endDate)
                .duration(20)
                .capacity(60)
                .build();

        bookingRequest = ClassBookingRequestDto.builder()
                .gymClassId(1L)
                .memberName("John Doe")
                .participationDate(startDate.plusDays(5))
                .build();

        gymClassRequest = GymClassRequestDto.builder()
                .name("Yoga Class")
                .startDate(today) // Class starts today
                .endDate(today.plusDays(30)) // Class ends in 30 days
                .startTime(LocalTime.of(10, 30))
                .duration(20)
                .capacity(60)
                .build();
    }

    /**
     * Gym Class Creation Tests
     */

    @Test
    void createClass_ValidInput_ReturnsSuccess() {
        when(gymClassRepository.countOverlappingClasses(gymClassRequest.getStartDate(), gymClassRequest.getEndDate())).thenReturn(0L);
        when(gymClassRepository.save(any(GymClass.class))).thenReturn(gymClass);

        GenericResponse<GymClassResponseDto> response = gymService.createClass(gymClassRequest);

        assertNotNull(response.getData(), "Response data should not be null");
        assertEquals("Yoga Class", response.getData().getName());
        assertEquals(HttpStatus.CREATED.value(), response.getStatusCode(), "Expected HTTP status: CREATED");
    }

    @Test
    void createClass_OverlappingClass_ThrowsException() {
        when(gymClassRepository.countOverlappingClasses(gymClassRequest.getStartDate(), gymClassRequest.getEndDate())).thenReturn(1L);

        GenericResponse<GymClassResponseDto> response = gymService.createClass(gymClassRequest);

        assertNull(response.getData());
        assertEquals("A class is already scheduled in this date range", response.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode());
    }

    @Test
    void createClass_EndDateBeforeStartDate_ThrowsException() {
        gymClassRequest.setStartDate(LocalDate.of(2025, 1, 31));
        gymClassRequest.setEndDate(LocalDate.of(2025, 1, 1));

        GenericResponse<GymClassResponseDto> response = gymService.createClass(gymClassRequest);

        assertNull(response.getData());
        assertEquals("End date must be after start date", response.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode());
    }

    @Test
    void createClass_Exception_ReturnsInternalServerError() {
        when(gymClassRepository.countOverlappingClasses(any(), any())).thenReturn(0L);
        when(gymClassRepository.save(any())).thenThrow(new RuntimeException("An unexpected error occurred.") {});

        GenericResponse<GymClassResponseDto> response = gymService.createClass(gymClassRequest);

        assertNull(response.getData());
        assertEquals("An unexpected error occurred.", response.getMessage());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getStatusCode());
    }

    /**
     * Class Booking Tests.
     */

    @Test
    void bookClass_ValidBooking_ReturnsSuccess() {
        ClassBooking savedBooking = new ClassBooking();
        savedBooking.setId(1L);
        savedBooking.setMemberName("John Doe");
        savedBooking.setGymClass(gymClass);
        savedBooking.setParticipationDate(bookingRequest.getParticipationDate());

        when(gymClassRepository.findById(1L)).thenReturn(Optional.of(gymClass));
        when(bookingRepository.countByGymClassAndParticipationDate(gymClass, bookingRequest.getParticipationDate())).thenReturn(0L);
        when(bookingRepository.save(any(ClassBooking.class))).thenReturn(savedBooking);

        GenericResponse<ClassBookingResponseDto> response = gymService.bookClass(bookingRequest);

        assertNotNull(response.getData());
        assertEquals("John Doe", response.getData().getMemberName());
        assertEquals(HttpStatus.CREATED.value(), response.getStatusCode());
    }

    @Test
    void bookClass_ParticipationDateOutsideClassSchedule_ReturnsError() {
        bookingRequest.setParticipationDate(endDate.plusDays(1));
        when(gymClassRepository.findById(1L)).thenReturn(Optional.of(gymClass));
        GenericResponse<ClassBookingResponseDto> response = gymService.bookClass(bookingRequest);

        assertNull(response.getData());
        assertTrue(response.getMessage().contains("Participation date must be within the class schedule range"));
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode());
    }

    @Test
    void bookClass_ClassCapacityExceeded_ReturnsError() {
        when(gymClassRepository.findById(1L)).thenReturn(Optional.of(gymClass));
        when(bookingRepository.countByGymClassAndParticipationDate(gymClass, bookingRequest.getParticipationDate())).thenReturn(60L); // Simulating full capacity

        GenericResponse<ClassBookingResponseDto> response = gymService.bookClass(bookingRequest);

        assertNull(response.getData());
        assertEquals(ResponseConstants.ERROR_CAPACITY_EXCEEDED, response.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCode());
    }

    @Test
    void bookClass_UnexpectedError_ReturnsInternalServerError() {
        when(gymClassRepository.findById(1L)).thenThrow(new RuntimeException("Unexpected error"));

        GenericResponse<ClassBookingResponseDto> response = gymService.bookClass(bookingRequest);

        assertNull(response.getData());
        assertEquals(ResponseConstants.ERROR_BOOKING_FAILED, response.getMessage());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getStatusCode());
    }


    /**
     * Search Bookings Tests
     */
    @Test
    void searchBookings_Found_ReturnsBookingsList() {
        // Given
        String memberName = "John Doe";
        List<ClassBooking> mockBookings = List.of(
                new ClassBooking(1L, "John Doe", null, startDate),
                new ClassBooking(2L, "John Doe", null, startDate.plusDays(1))
        );

        // Mock the repository call
        when(bookingRepository.findAll(any(Specification.class))).thenReturn(mockBookings);

        // When
        GenericResponse<List<ClassBooking>> response = gymService.searchBookings(memberName, startDate, endDate);

        // Then
        assertNotNull(response.getData(), "Response data should not be null");
        assertEquals(2, response.getData().size(), "Expected two bookings in the response");
        assertEquals("John Doe", response.getData().getFirst().getMemberName());
        assertEquals(HttpStatus.OK.value(), response.getStatusCode(), "Expected HTTP status OK");
    }

    @Test
    void searchBookings_NoBookingsFound_ReturnsEmptyList() {
        // Given
        String memberName = "Jane Doe";

        // Mock empty response
        when(bookingRepository.findAll(any(Specification.class))).thenReturn(Collections.emptyList());

        // When
        GenericResponse<List<ClassBooking>> response = gymService.searchBookings(memberName, startDate, endDate);

        // Then
        assertNotNull(response.getData(), "Response data should not be null");
        assertTrue(response.getData().isEmpty(), "Response list should be empty");
        assertEquals(ResponseConstants.NO_BOOKINGS_FOUND, response.getMessage(), "Expected NO_BOOKINGS_FOUND message");
        assertEquals(HttpStatus.OK.value(), response.getStatusCode(), "Expected HTTP status OK");
    }

    @Test
    void searchBookings_UnexpectedError_ReturnsInternalServerError() {
        // Given
        String memberName = "John Doe";

        // Simulate an exception
        when(bookingRepository.findAll(any(Specification.class))).thenThrow(new RuntimeException("Database error"));

        // When
        GenericResponse<List<ClassBooking>> response = gymService.searchBookings(memberName, startDate, endDate);

        // Then
        assertNull(response.getData(), "Response data should be null when an error occurs");
        assertEquals(ResponseConstants.DEFAULT_ERROR_MESSAGE, response.getMessage(), "Expected default error message");
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getStatusCode(), "Expected HTTP status INTERNAL_SERVER_ERROR");
    }
}