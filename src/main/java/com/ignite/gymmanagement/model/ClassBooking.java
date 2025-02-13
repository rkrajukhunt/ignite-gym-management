/**
 * Author: Raju Khunt
 * Created by: rajukhunt on Date:13/02/25
 */

package com.ignite.gymmanagement.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "class_booking", indexes = {
        @Index(name = "idx_booking_member_name", columnList = "member_name"),
        @Index(name = "idx_booking_participation_date", columnList = "participation_date"),
        @Index(name = "idx_booking_composite", columnList = "member_name, participation_date")
})
@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class ClassBooking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "member_name", nullable = false)
    private String memberName;  // Name of the member booking the class

    @ManyToOne()
    @JoinColumn(name = "gym_class_id", nullable = false) // Explicit foreign key definition
    private GymClass gymClass;  // Reference to the class being booked

    @Column(name = "participation_date", nullable = false)
    private LocalDate participationDate; // Date on which the member will participate in the class

}
