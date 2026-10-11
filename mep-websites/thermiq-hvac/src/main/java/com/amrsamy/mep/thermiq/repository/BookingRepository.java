package com.amrsamy.mep.thermiq.repository;

import com.amrsamy.mep.thermiq.model.Booking;
import com.amrsamy.mep.thermiq.model.TimeSlot;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    long countByDateAndSlot(LocalDate date, TimeSlot slot);

    @Query("""
            select b.date as date, b.slot as slot, count(b) as total
            from Booking b
            where b.date between :from and :to
            group by b.date, b.slot""")
    List<SlotCount> countPerSlot(@Param("from") LocalDate from, @Param("to") LocalDate to);

    interface SlotCount {
        LocalDate getDate();
        TimeSlot getSlot();
        long getTotal();
    }
}
