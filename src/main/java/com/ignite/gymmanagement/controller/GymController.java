/**
 * Author: Raju Khunt
 * Created by: rajukhunt on Date:13/02/25
 */

package com.ignite.gymmanagement.controller;

import com.ignite.gymmanagement.dto.ClassBookingRequestDto;
import com.ignite.gymmanagement.dto.ClassBookingResponseDto;
import com.ignite.gymmanagement.dto.GymClassRequestDto;
import com.ignite.gymmanagement.dto.GymClassResponseDto;
import com.ignite.gymmanagement.model.ClassBooking;
import com.ignite.gymmanagement.service.GymService;
import com.ignite.gymmanagement.util.GenericResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@Validated
@AllArgsConstructor
@RequestMapping("/api/v1")
public class GymController {

    private final GymService gymService;

    /**
     * Creates a new GymClass.
     */
    @PostMapping("/classes")
    public ResponseEntity<GenericResponse<GymClassResponseDto>> createClass(
            @Valid @RequestBody GymClassRequestDto gymClassRequest) {
        log.info("Received request to create class: {}", gymClassRequest);

        GenericResponse<GymClassResponseDto> response = gymService.createClass(gymClassRequest);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    /**
     * Searches for bookings based on optional filters.
     */
    @GetMapping("/bookings/search")
    public ResponseEntity<GenericResponse<List<ClassBooking>>> searchBookings(
            @RequestParam(required = false) String memberName,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate) {

        log.info("Received request to search bookings - Member: {}, StartDate: {}, EndDate: {}",
                memberName, startDate, endDate);

        GenericResponse<List<ClassBooking>> response = gymService.searchBookings(memberName, startDate, endDate);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    /**
     * Books a class for a member.
     */
    @PostMapping("/bookings")
    public ResponseEntity<GenericResponse<ClassBookingResponseDto>> bookClass(
            @Valid @RequestBody ClassBookingRequestDto bookingRequest) {

        log.info("Received request to book class: {}", bookingRequest);
        GenericResponse<ClassBookingResponseDto> response = gymService.bookClass(bookingRequest);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }
}
