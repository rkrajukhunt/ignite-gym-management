/**
 * Author: Raju Khunt
 * Created by: rajukhunt on Date:13/02/25
 */

package com.ignite.gymmanagement.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "gym_class")
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GymClass {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;        // Name of the class

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate; // Start date of the class schedule

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;   // End date of the class schedule

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime; // Start time of the class each day

    @Column(name = "duration", nullable = false)
    private int duration;        // Duration of the class in minutes

    @Column(name = "capacity", nullable = false)
    private int capacity;        // Maximum number of participants allowed
}
