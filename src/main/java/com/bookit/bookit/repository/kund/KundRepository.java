package com.bookit.bookit.repository.kund;

import com.bookit.bookit.entity.kund.Kund;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KundRepository extends JpaRepository<Kund, Integer> {
}
