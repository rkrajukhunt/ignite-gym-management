/**
 * Author: Raju Khunt
 * Created by: rajukhunt on Date:13/02/25
 */

package com.ignite.gymmanagement.service;

import com.ignite.gymmanagement.dto.ClassBookingRequestDto;
import com.ignite.gymmanagement.dto.ClassBookingResponseDto;
import com.ignite.gymmanagement.dto.GymClassRequestDto;
import com.ignite.gymmanagement.dto.GymClassResponseDto;
import com.ignite.gymmanagement.exception.CustomException;
import com.ignite.gymmanagement.model.ClassBooking;
import com.ignite.gymmanagement.model.GymClass;
import com.ignite.gymmanagement.repository.BookingRepository;
import com.ignite.gymmanagement.repository.GymClassRepository;
import com.ignite.gymmanagement.specifications.BookingSpecifications;
import com.ignite.gymmanagement.util.GenericResponse;
import com.ignite.gymmanagement.util.ResponseConstants;
import com.ignite.gymmanagement.util.ResponseUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GymService {
    private final GymClassRepository gymClassRepository;
    private final BookingRepository bookingRepository;

    /**
     * Searches for class bookings based on member name and date range.
     */
    public GenericResponse<List<ClassBooking>> searchBookings(String memberName, LocalDate startDate, LocalDate endDate) {
        try {
            // List<ClassBooking> bookings = bookingRepository.findBookings(memberName, startDate, endDate);
            Specification<ClassBooking> spec = BookingSpecifications.filterBookings(memberName, startDate, endDate);
            List<ClassBooking> bookings = bookingRepository.findAll(spec);

            if (bookings.isEmpty()) {
                return ResponseUtils.success(List.of(), ResponseConstants.NO_BOOKINGS_FOUND, HttpStatus.OK);
            }

            return ResponseUtils.success(bookings, ResponseConstants.DEFAULT_SUCCESS_MESSAGE, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error fetching bookings: {}", e.getMessage(), e);
            return ResponseUtils.error(ResponseConstants.DEFAULT_ERROR_MESSAGE, HttpStatus.INTERNAL_SERVER_ERROR, ResponseConstants.BOOKING_ERROR_CODE);
        }
    }

    /**
     * Creates a new GymClass.
     */
    public GenericResponse<GymClassResponseDto> createClass(GymClassRequestDto gymClassRequest) {
        try {
            // Validation: Ensure endDate is after startDate
            if (gymClassRequest.getEndDate().isBefore(gymClassRequest.getStartDate())) {
                return ResponseUtils.error("End date must be after start date", HttpStatus.BAD_REQUEST, ResponseConstants.CLASS_CREATION_ERROR_CODE);
            }

            // Check if any existing class overlaps with the requested schedule
            long existingClassCount = gymClassRepository.countOverlappingClasses(
                    gymClassRequest.getStartDate(), gymClassRequest.getEndDate()
            );

            if (existingClassCount > 0) {
                return ResponseUtils.error("A class is already scheduled in this date range", HttpStatus.BAD_REQUEST, ResponseConstants.CLASS_CREATION_ERROR_CODE);
            }

            // Convert DTO to Entity
            GymClass gymClass = GymClass.builder()
                    .name(gymClassRequest.getName())
                    .capacity(gymClassRequest.getCapacity())
                    .startDate(gymClassRequest.getStartDate())
                    .endDate(gymClassRequest.getEndDate())
                    .startTime(gymClassRequest.getStartTime())
                    .duration(gymClassRequest.getDuration())
                    .build();

            GymClass savedClass = gymClassRepository.save(gymClass);

            GymClassResponseDto responseDto = GymClassResponseDto.builder()
                    .id(savedClass.getId())
                    .name(savedClass.getName())
                    .capacity(savedClass.getCapacity())
                    .startDate(savedClass.getStartDate())
                    .endDate(savedClass.getEndDate())
                    .startTime(savedClass.getStartTime())
                    .duration(savedClass.getDuration())
                    .build();

            log.info("Class created successfully: {}", responseDto);
            return ResponseUtils.success(responseDto, ResponseConstants.CLASS_CREATED_SUCCESS, HttpStatus.CREATED);
        } catch (Exception e) {
            log.error("Error creating class: {}", e.getMessage(), e);
            return ResponseUtils.error(ResponseConstants.DEFAULT_ERROR_MESSAGE, HttpStatus.INTERNAL_SERVER_ERROR, ResponseConstants.CLASS_CREATION_ERROR_CODE);
        }
    }

    /**
     * Books a class for a member.
     */
    public GenericResponse<ClassBookingResponseDto> bookClass(ClassBookingRequestDto bookingRequest) {
        try {
            // Validate Gym Class Existence
            GymClass gymClass = gymClassRepository.findById(bookingRequest.getGymClassId())
                    .orElseThrow(() -> {
                        log.error("Class not found for booking: {}", bookingRequest);
                        return new CustomException(ResponseConstants.ERROR_CLASS_NOT_FOUND, ResponseConstants.BOOKING_ERROR_CODE);
                    });


            // Validate that participation date is within class start and end date
            if (bookingRequest.getParticipationDate().isBefore(gymClass.getStartDate()) ||
                    bookingRequest.getParticipationDate().isAfter(gymClass.getEndDate())) {
                log.error("Participation date {} is outside the allowed range for class {} ({} to {})",
                        bookingRequest.getParticipationDate(), gymClass.getId(), gymClass.getStartDate(), gymClass.getEndDate());

                return ResponseUtils.error("Participation date must be within the class schedule range (" +
                        gymClass.getStartDate() + " to " + gymClass.getEndDate() + ")", HttpStatus.BAD_REQUEST, ResponseConstants.BOOKING_ERROR_CODE);
            }


            // Check Class Capacity
            long currentBookings = bookingRepository.countByGymClassAndParticipationDate(
                    gymClass, bookingRequest.getParticipationDate()
            );

            log.info("Current bookings for class {} on {}: {}", gymClass.getId(), bookingRequest.getParticipationDate(), currentBookings);
            if (currentBookings >= gymClass.getCapacity()) {
                log.error("Class capacity exceeded for class {} on {}", gymClass.getId(), bookingRequest.getParticipationDate());
                return ResponseUtils.error(ResponseConstants.ERROR_CAPACITY_EXCEEDED, HttpStatus.BAD_REQUEST, ResponseConstants.BOOKING_ERROR_CODE);
            }

            // Create and Save Booking
            ClassBooking booking = new ClassBooking();
            booking.setGymClass(gymClass);
            booking.setMemberName(bookingRequest.getMemberName());
            booking.setParticipationDate(bookingRequest.getParticipationDate());

            ClassBooking savedBooking = bookingRepository.save(booking);

            ClassBookingResponseDto responseDto = ClassBookingResponseDto.builder()
                    .id(savedBooking.getId())
                    .memberName(savedBooking.getMemberName())
                    .gymClassName(gymClass.getName())
                    .participationDate(savedBooking.getParticipationDate())
                    .build();

            log.info("Class booked successfully: {}", responseDto);
            return ResponseUtils.success(responseDto, ResponseConstants.BOOKING_SUCCESS, HttpStatus.CREATED);
        } catch (CustomException ex) {
            log.error("Booking failed: {}", ex.getMessage());
            return ResponseUtils.error(ex.getMessage(), HttpStatus.BAD_REQUEST, ResponseConstants.BOOKING_ERROR_CODE);
        } catch (Exception e) {
            log.error("Unexpected error during booking: {}", e.getMessage(), e);
            return ResponseUtils.error(ResponseConstants.ERROR_BOOKING_FAILED, HttpStatus.INTERNAL_SERVER_ERROR, ResponseConstants.BOOKING_ERROR_CODE);
        }
    }}
