package com.bookit.bookit.repository.tjänst;

import com.bookit.bookit.entity.städare.Städare;
import com.bookit.bookit.entity.tjänst.Tjänst;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TjänstRepository extends JpaRepository<Tjänst, Integer> {
}
