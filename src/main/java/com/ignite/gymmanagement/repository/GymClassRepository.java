/**
 * Author: Raju Khunt
 * Created by: rajukhunt on Date:13/02/25
 */

package com.ignite.gymmanagement.repository;

import com.ignite.gymmanagement.model.GymClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;

public interface GymClassRepository extends JpaRepository<GymClass, Long> {

    boolean existsByStartDate(LocalDate startDate);

    // Check if any class exists that overlaps with the requested schedule
    @Query("SELECT COUNT(c) FROM GymClass c WHERE " +
            "(c.startDate <= :endDate AND c.endDate >= :startDate)")
    long countOverlappingClasses(@Param("startDate") LocalDate startDate,
                                 @Param("endDate") LocalDate endDate);
}
