package com.aero.ops.service;

import com.aero.ops.model.Vol;
import com.aero.ops.repository.VolRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VolService {

    private final VolRepository volRepository;

    public VolService(VolRepository volRepository) {
        this.volRepository = volRepository;
    }

    public List<Vol> getAll() {
        return volRepository.findAll();
    }

    public Vol getById(Long id) {
        return volRepository.findById(id).orElse(null);
    }

    public Vol create(Vol vol) {
        return volRepository.save(vol);
    }

    public Vol update(Vol vol) {
        return volRepository.save(vol);
    }

    public void delete(Long id) {
        volRepository.deleteById(id);
    }
}
