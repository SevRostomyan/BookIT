package com.bookit.bookit.repository.städare;

import com.bookit.bookit.entity.bokning.Bokning;
import com.bookit.bookit.entity.städare.Städare;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StädareRepository extends JpaRepository<Städare, Integer> {

}
