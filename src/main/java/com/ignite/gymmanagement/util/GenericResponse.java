/**
 * Author: Raju Khunt
 * Created by: rajukhunt on Date:13/02/25
 */

package com.ignite.gymmanagement.util;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Value;
import java.time.LocalDateTime;

/**
 * A standard response object for all API interactions.
 *
 * @param <T> The type of the data being returned.
 */

@Getter
@Value
@Data
@Builder
public class GenericResponse<T> {

    T data;                   // The response payload (optional)
    String message;           // A user-friendly message
    boolean success;          // Indicates operation status
    int statusCode;           // HTTP status code
    String errorCode;         // Optional error code for debugging
    LocalDateTime timestamp;  // Response generation time

}
