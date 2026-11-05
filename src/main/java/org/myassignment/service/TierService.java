package org.myassignment.service;

import lombok.RequiredArgsConstructor;
import org.myassignment.entity.Tier;
import org.myassignment.exception.BadRequestException;
import org.myassignment.exception.ResourceNotFoundException;
import org.myassignment.repository.TierRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TierService {

    private final TierRepository tierRepository;

    public List<Tier> getAll() {
        return tierRepository.findAll();
    }

    public Tier getById(Long id) {
        return tierRepository.findById(id).orElseThrow(() -> ResourceNotFoundException.of("Tier", id));
    }

    @Transactional
    public Tier create(String name) {
        String clean = requireName(name);
        if (tierRepository.existsByName(clean)) {
            throw new BadRequestException("Tier already exists: " + clean);
        }
        return tierRepository.save(Tier.builder().name(clean).build());
    }

    @Transactional
    public Tier rename(Long id, String name) {
        Tier tier = getById(id);
        String clean = requireName(name);
        if (!clean.equals(tier.getName()) && tierRepository.existsByName(clean)) {
            throw new BadRequestException("Tier already exists: " + clean);
        }
        tier.setName(clean);
        return tier;
    }

    @Transactional
    public void delete(Long id) {
        Tier tier = getById(id);
        if (!tier.getPackages().isEmpty()) {
            throw new BadRequestException("Tier is used by travel packages and cannot be deleted.");
        }
        tierRepository.delete(tier);
    }

    private String requireName(String name) {
        if (name == null || name.isBlank()) {
            throw new BadRequestException("Tier name is required.");
        }
        return name.trim();
    }
}
