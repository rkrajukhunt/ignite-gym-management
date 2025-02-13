/**
 * Author: Raju Khunt
 * Created by: rajukhunt on Date:13/02/25
 */
 
package com.ignite.gymmanagement.dto;

import jakarta.persistence.Column;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
public class GymClassResponseDto {

    private Long id;
    private String name;
    private int capacity;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalTime startTime;
    private int duration;

}