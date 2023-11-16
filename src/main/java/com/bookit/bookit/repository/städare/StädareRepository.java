package com.bookit.bookit.repository.städare;

import com.bookit.bookit.entity.bokning.Bokning;
import com.bookit.bookit.entity.städare.Städare;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StädareRepository extends JpaRepository<Städare, Integer> {



    //In this query, :start and :end represent the start and end times of the 2-hour slot.
    @Query("SELECT s FROM Städare s WHERE NOT EXISTS (" +
            "SELECT b FROM Bokning b WHERE b.städare = s AND " +
            "(b.bookingTime < :end AND b.endTime > :start))")
    List<Städare> findAvailableCleaners(@Param("start") LocalDateTime start,
                                        @Param("end") LocalDateTime end);



}
