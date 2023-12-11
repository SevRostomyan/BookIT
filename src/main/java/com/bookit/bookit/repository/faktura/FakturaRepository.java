package com.bookit.bookit.repository.faktura;

import com.bookit.bookit.entity.faktura.Faktura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FakturaRepository extends JpaRepository<Faktura, Integer> {
    List<Faktura> findAllByKundId(Integer kundId);
}
