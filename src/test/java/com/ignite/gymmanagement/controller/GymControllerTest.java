package com.ignite.gymmanagement.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ignite.gymmanagement.dto.ClassBookingRequestDto;
import com.ignite.gymmanagement.dto.ClassBookingResponseDto;
import com.ignite.gymmanagement.dto.GymClassRequestDto;
import com.ignite.gymmanagement.dto.GymClassResponseDto;
import com.ignite.gymmanagement.model.ClassBooking;
import com.ignite.gymmanagement.service.GymService;
import com.ignite.gymmanagement.util.GenericResponse;
import com.ignite.gymmanagement.util.ResponseUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(GymController.class)
@ExtendWith(MockitoExtension.class)
public class GymControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GymService gymService;

    @InjectMocks
    private GymController gymController;

    @Autowired
    private ObjectMapper objectMapper;

    private GymClassRequestDto gymClassRequest;
    private ClassBookingRequestDto bookingRequest;

    /**
     * Initializes test data before each test execution.
     */
    @BeforeEach
    void setUp() {
        Clock clock = Clock.systemDefaultZone(); // Use system time
        LocalDate today = LocalDate.now(clock); // Get today's date dynamically

        LocalDate startDate = today.plusDays(1); // Start date is tomorrow
        LocalDate endDate = today.plusMonths(1); // End date is one month from today

        gymClassRequest = GymClassRequestDto.builder()
                .name("Yoga Class")
                .startDate(startDate)
                .endDate(endDate)
                .startTime(LocalTime.of(10, 30))
                .duration(20)
                .capacity(60)
                .build();

        bookingRequest = ClassBookingRequestDto.builder()
                .gymClassId(1L)
                .memberName("John Doe")
                .participationDate(startDate.plusDays(5))
                .build();
    }

    /**
     * Test: Class Test
     */
    @Test
    void createClass_ValidRequest_ReturnsSuccess() throws Exception {
        GymClassResponseDto responseDto = GymClassResponseDto.builder()
                .id(1L)
                .name(gymClassRequest.getName())
                .capacity(gymClassRequest.getCapacity())
                .startDate(gymClassRequest.getStartDate())
                .endDate(gymClassRequest.getEndDate())
                .build();

        GenericResponse<GymClassResponseDto> serviceResponse =
                ResponseUtils.success(responseDto, "Class created successfully", HttpStatus.CREATED);

        when(gymService.createClass(any(GymClassRequestDto.class))).thenReturn(serviceResponse);

        mockMvc.perform(post("/api/v1/classes")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(gymClassRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.name").value("Yoga Class"))
                .andExpect(jsonPath("$.message").value("Class created successfully"));
    }

    @Test
    void createClass_PastStartDate_ReturnsBadRequest() throws Exception {
        GymClassRequestDto invalidRequest = gymClassRequest;
        invalidRequest.setStartDate(LocalDate.now().minusDays(1));
        invalidRequest.setEndDate(LocalDate.now().plusDays(1));

        mockMvc.perform(post("/api/v1/classes")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status")
                        .value(400))
                .andExpect(jsonPath("$.error")
                        .value("Bad Request"))
                .andExpect(jsonPath("$.errors.startDate")
                        .value("Start date must be today or in the future"));
    }

    @Test
    void createClass_InvalidCapacity_ReturnsBadRequest() throws Exception {
        GymClassRequestDto invalidRequest = gymClassRequest;
        invalidRequest.setCapacity(-1);

        mockMvc.perform(post("/api/v1/classes")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status")
                        .value(400))
                .andExpect(jsonPath("$.error")
                        .value("Bad Request"))
                .andExpect(jsonPath("$.errors.capacity")
                        .value("Capacity must be at least 1"));
    }

    @Test
    void createClass_InvalidRequest_ReturnsBadRequest() throws Exception {
        GymClassRequestDto invalidRequest = GymClassRequestDto.builder().build();

        mockMvc.perform(post("/api/v1/classes")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    /**
     * Test: Booking Test
     */
    @Test
    void bookClass_ValidRequest_ReturnsSuccess() throws Exception {
        ClassBookingResponseDto responseDto = ClassBookingResponseDto.builder()
                .id(1L)
                .memberName("John Doe")
                .gymClassName("Yoga")
                .participationDate(bookingRequest.getParticipationDate())
                .build();

        GenericResponse<ClassBookingResponseDto> serviceResponse =
                ResponseUtils.success(responseDto, "Booking successful", HttpStatus.CREATED);

        when(gymService.bookClass(any(ClassBookingRequestDto.class))).thenReturn(serviceResponse);

        mockMvc.perform(post("/api/v1/bookings")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(bookingRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.memberName").value("John Doe"))
                .andExpect(jsonPath("$.message").value("Booking successful"));
    }

    @Test
    void bookClass_ClassNotFound_ReturnsNotFound() throws Exception {
        when(gymService.bookClass(any(ClassBookingRequestDto.class))).
                thenReturn(ResponseUtils.error("Class not found", HttpStatus.NOT_FOUND, "BOOKING_ERROR"));

        mockMvc.perform(post("/api/v1/bookings")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(bookingRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message")
                        .value("Class not found"))
                .andExpect(jsonPath("$.errorCode")
                        .value("BOOKING_ERROR"));
    }

    @Test
    void bookClass_CapacityExceeded_ReturnsBadRequest() throws Exception {
        when(gymService.bookClass(any(ClassBookingRequestDto.class)))
                .thenReturn(ResponseUtils.error("Class capacity exceeded", HttpStatus.BAD_REQUEST, "BOOKING_ERROR"));

        mockMvc.perform(post("/api/v1/bookings")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(bookingRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message")
                        .value("Class capacity exceeded"))
                .andExpect(jsonPath("$.errorCode")
                        .value("BOOKING_ERROR"));
    }

    @Test
    void bookClass_ParticipationDateInPast_ReturnsBadRequest() throws Exception {
        ClassBookingRequestDto invalidRequest = bookingRequest;
        invalidRequest.setParticipationDate(LocalDate.now().minusDays(1));

        mockMvc.perform(post("/api/v1/bookings")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.participationDate")
                        .value("Participation date must be today or in the future"));
    }

    @Test
    void bookClass_InvalidRequest_ReturnsBadRequest() throws Exception {
        GymClassRequestDto invalidRequest = GymClassRequestDto.builder().build();

        mockMvc.perform(post("/api/v1/classes")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    /**
     * Test: Searching bookings with filters
     */
    @Test
    void searchBookings_ValidRequest_ReturnsResults() throws Exception {
        ClassBooking booking = new ClassBooking();
        booking.setId(1L);
        booking.setMemberName("John Doe");
        booking.setParticipationDate(LocalDate.now().plusDays(5));

        GenericResponse<List<ClassBooking>> serviceResponse =
                ResponseUtils.success(List.of(booking), "Bookings found", HttpStatus.OK);

        when(gymService.searchBookings(anyString(), any(), any())).thenReturn(serviceResponse);

        mockMvc.perform(get("/api/v1/bookings/search")
                        .param("memberName", "John Doe")
                        .param("startDate", LocalDate.now().toString())
                        .param("endDate", LocalDate.now().plusDays(10).toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].memberName").value("John Doe"))
                .andExpect(jsonPath("$.message").value("Bookings found"));
    }
}