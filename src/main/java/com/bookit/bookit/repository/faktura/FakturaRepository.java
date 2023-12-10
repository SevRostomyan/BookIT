package com.bookit.bookit.repository.faktura;

import com.bookit.bookit.entity.faktura.Faktura;
import com.bookit.bookit.entity.kund.Kund;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FakturaRepository extends JpaRepository<Faktura, Integer> {
}
