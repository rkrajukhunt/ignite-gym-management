/**
 * Author: Raju Khunt
 * Created by: rajukhunt on Date:13/02/25
 */

package com.ignite.gymmanagement.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class ClassBookingResponseDto {

    private Long id;
    private String memberName;
    private String gymClassName;
    private LocalDate participationDate;

}
