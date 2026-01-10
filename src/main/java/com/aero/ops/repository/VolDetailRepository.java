package com.aero.ops.repository;

import com.aero.ops.model.Vol;
import com.aero.ops.model.VolDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VolDetailRepository extends JpaRepository<VolDetail, Long> {
    List<VolDetail> findByVol(Vol vol);
    List<VolDetail> findByDateHeureDepartBetween(LocalDateTime start, LocalDateTime end);
}
