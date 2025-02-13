/**
 * Author: Raju Khunt
 * Created by: rajukhunt on Date: 13/02/25
 */

package com.ignite.gymmanagement.util;

import org.springframework.http.HttpStatus;
import java.time.LocalDateTime;

public class ResponseUtils {

    private ResponseUtils() {
        throw new UnsupportedOperationException("This utility class cannot be instantiated.");
    }

    /**
     * Creates a success response with data.
     *
     * @param data The response payload.
     * @param <T>  The type of the response payload.
     * @return A success response object.
     */
    public static <T> GenericResponse<T> success(T data) {
        return buildResponse(data, ResponseConstants.DEFAULT_SUCCESS_MESSAGE, true, HttpStatus.OK, null);
    }

    /**
     * Creates a success response with a custom message.
     *
     * @param message The custom success message.
     * @param <T>     The type of the response payload.
     * @return A success response object.
     */
    public static <T> GenericResponse<T> success(String message) {
        return buildResponse(null, message, true, HttpStatus.OK, null);
    }

    /**
     * Creates a custom success response.
     *
     * @param data      The response payload.
     * @param message   The success message.
     * @param status    The HTTP status code.
     * @param <T>       The type of the response payload.
     * @return A success response object.
     */
    public static <T> GenericResponse<T> success(T data, String message, HttpStatus status) {
        return buildResponse(data, message, true, status, null);
    }

    /**
     * Creates a standard error response with a custom message.
     *
     * @param message The error message.
     * @param <T>     The type of the response payload.
     * @return An error response object.
     */
    public static <T> GenericResponse<T> error(String message) {
        return buildResponse(null, message, false, HttpStatus.BAD_REQUEST, ResponseConstants.DEFAULT_ERROR_MESSAGE);
    }

    /**
     * Creates a custom error response.
     *
     * @param message   The error message.
     * @param status    The HTTP status code.
     * @param <T>       The type of the response payload.
     * @return An error response object.
     */
    public static <T> GenericResponse<T> error(String message, HttpStatus status) {
        return buildResponse(null, message, false, status, null);
    }

    /**
     * Creates a custom error response.
     *
     * @param message   The error message.
     * @param status    The HTTP status code.
     * @param errorCode The specific error code.
     * @param <T>       The type of the response payload.
     * @return An error response object.
     */
    public static <T> GenericResponse<T> error(String message, HttpStatus status, String errorCode) {
        return buildResponse(null, message, false, status, errorCode);
    }

    /**
     * Creates a detailed error response with data.
     *
     * @param data      The response payload.
     * @param message   The error message.
     * @param status    The HTTP status code.
     * @param errorCode The specific error code.
     * @param <T>       The type of the response payload.
     * @return An error response object.
     */
    public static <T> GenericResponse<T> error(T data, String message, HttpStatus status, String errorCode) {
        return buildResponse(data, message, false, status, errorCode);
    }

    /**
     * Core builder method for constructing response objects.
     *
     * @param data      The response payload.
     * @param message   The success/error message.
     * @param success   The operation status.
     * @param status    The HTTP status code.
     * @param errorCode Optional error code.
     * @param <T>       The type of the response payload.
     * @return A GenericResponse object.
     */
    private static <T> GenericResponse<T> buildResponse(T data, String message, boolean success, HttpStatus status, String errorCode) {
        return GenericResponse.<T>builder()
                .data(data)
                .message(message)
                .success(success)
                .statusCode(status.value()) // Fix: Ensure HttpStatus is converted to int
                .errorCode(success ? null : errorCode) // Fix: Ensure errorCode is only for errors
                .timestamp(LocalDateTime.now())
                .build();
    }
}