/**
 * Author: Raju Khunt
 * Created by: rajukhunt on Date:13/02/25
 */

package com.ignite.gymmanagement.util;

public class ResponseConstants {
    private ResponseConstants() {
        throw new UnsupportedOperationException("This utility class cannot be instantiated.");
    }

    // Success Messages
    public static final String DEFAULT_SUCCESS_MESSAGE = "Operation completed successfully.";
    public static final String CLASS_CREATED_SUCCESS = "Class created successfully!";
    public static final String BOOKING_SUCCESS = "Class booked successfully!";
    public static final String NO_BOOKINGS_FOUND = "No bookings found for the given criteria.";

    // Common Error Messages
    public static final String DEFAULT_ERROR_MESSAGE = "An unexpected error occurred.";
    public static final String ERROR_CLASS_NOT_FOUND = "Class not found.";
    public static final String ERROR_BOOKING_FAILED = "Booking could not be created.";
    public static final String ERROR_CAPACITY_EXCEEDED = "Class capacity exceeded.";

    public static final String BOOKING_ERROR_CODE = "BOOKING_ERROR";
    public static final String CLASS_CREATION_ERROR_CODE = "CLASS_CREATION_ERROR";
}
