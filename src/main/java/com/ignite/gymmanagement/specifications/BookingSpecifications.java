/**
 * Author: Raju Khunt
 * Created by: rajukhunt on Date:13/02/25
 */

package com.ignite.gymmanagement.specifications;

import com.ignite.gymmanagement.model.ClassBooking;

import org.springframework.data.jpa.domain.Specification;
import java.time.LocalDate;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
public class BookingSpecifications {

    /**
     * Builds a dynamic JPA Specification for filtering bookings.
     */
    public static Specification<ClassBooking> filterBookings(String memberName, LocalDate startDate, LocalDate endDate) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Filter by member name (case-insensitive)
            if (memberName != null && !memberName.isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("memberName")), "%" + memberName.toLowerCase() + "%"));
            }

            // Filter by start date
            if (startDate != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("participationDate"), startDate));
            }

            // Filter by end date
            if (endDate != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("participationDate"), endDate));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}