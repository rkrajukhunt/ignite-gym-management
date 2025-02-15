/**
 * Author: Raju Khunt
 * Created by: rajukhunt on Date:13/02/25
 */

package com.ignite.gymmanagement.dto;

import jakarta.validation.constraints.FutureOrPresent;
import lombok.Builder;
import lombok.Data;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

@Data
@Builder
public class ClassBookingRequestDto {

    @NotNull(message = "Member name is required")
    private String memberName;

    @NotNull(message = "Gym class ID is required")
    private Long gymClassId;

    @NotNull(message = "Participation date is required")
    @FutureOrPresent(message = "Participation date must be today or in the future")
    private LocalDate participationDate;

}
