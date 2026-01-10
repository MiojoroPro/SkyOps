package com.aero.ops.service;

import com.aero.ops.model.Vol;
import com.aero.ops.model.VolDetail;
import com.aero.ops.repository.VolDetailRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class VolDetailService {

    private final VolDetailRepository volDetailRepository;

    public VolDetailService(VolDetailRepository volDetailRepository) {
        this.volDetailRepository = volDetailRepository;
    }

    public List<VolDetail> getAll() {
        return volDetailRepository.findAll();
    }

    public VolDetail getById(Long id) {
        return volDetailRepository.findById(id).orElse(null);
    }

    public List<VolDetail> getByVol(Vol vol) {
        return volDetailRepository.findByVol(vol);
    }

    public List<VolDetail> getByDateRange(LocalDateTime start, LocalDateTime end) {
        return volDetailRepository.findByDateHeureDepartBetween(start, end);
    }

    public VolDetail create(VolDetail volDetail) {
        return volDetailRepository.save(volDetail);
    }

    public VolDetail update(VolDetail volDetail) {
        return volDetailRepository.save(volDetail);
    }

    public void delete(Long id) {
        volDetailRepository.deleteById(id);
    }
}
