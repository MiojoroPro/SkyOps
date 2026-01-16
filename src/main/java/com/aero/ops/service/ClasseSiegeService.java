package com.aero.ops.service;

import com.aero.ops.model.ClasseSiege;
import com.aero.ops.repository.ClasseSiegeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClasseSiegeService {

    private final ClasseSiegeRepository classeSiegeRepository;

    public ClasseSiegeService(ClasseSiegeRepository classeSiegeRepository) {
        this.classeSiegeRepository = classeSiegeRepository;
    }

    public List<ClasseSiege> getAll() {
        return classeSiegeRepository.findAll();
    }

    public ClasseSiege getById(Long id) {
        return classeSiegeRepository.findById(id).orElse(null);
    }

    public Optional<ClasseSiege> getByCode(String code) {
        return classeSiegeRepository.findByCode(code);
    }

    public ClasseSiege create(ClasseSiege classeSiege) {
        return classeSiegeRepository.save(classeSiege);
    }

    public ClasseSiege update(ClasseSiege classeSiege) {
        return classeSiegeRepository.save(classeSiege);
    }

    public void delete(Long id) {
        classeSiegeRepository.deleteById(id);
    }
}
