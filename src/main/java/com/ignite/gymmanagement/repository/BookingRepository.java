/**
 * Author: Raju Khunt
 * Created by: rajukhunt on Date:13/02/25
 */

package com.ignite.gymmanagement.repository;

import com.ignite.gymmanagement.model.ClassBooking;
import com.ignite.gymmanagement.model.GymClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface BookingRepository extends JpaRepository<ClassBooking, Long>, JpaSpecificationExecutor<ClassBooking> {

    /**
     * Counts bookings for a given class on a specific date.
     */
    long countByGymClassAndParticipationDate(GymClass gymClass, LocalDate participationDate);

    /**
     * Fetches bookings based on optional filters (member name, start date, end date).
     */
    @Query("SELECT b FROM ClassBooking b " +
            "WHERE (:memberName IS NULL OR LOWER(b.memberName) LIKE LOWER(CONCAT('%', :memberName, '%'))) " +
            "AND (:startDate IS NULL OR b.participationDate >= :startDate) " +
            "AND (:endDate IS NULL OR b.participationDate <= :endDate)")
    List<ClassBooking> findBookings(@Param("memberName") String memberName,
                                    @Param("startDate") LocalDate startDate,
                                    @Param("endDate") LocalDate endDate);
}
