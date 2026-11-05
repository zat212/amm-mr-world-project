package org.myassignment.service;

import lombok.RequiredArgsConstructor;
import org.myassignment.entity.Theme;
import org.myassignment.exception.BadRequestException;
import org.myassignment.exception.ResourceNotFoundException;
import org.myassignment.repository.ThemeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ThemeService {

    private final ThemeRepository themeRepository;

    public List<Theme> getAll() {
        return themeRepository.findAll();
    }

    public Theme getById(Long id) {
        return themeRepository.findById(id).orElseThrow(() -> ResourceNotFoundException.of("Theme", id));
    }

    @Transactional
    public Theme create(String name) {
        String clean = requireName(name);
        if (themeRepository.existsByName(clean)) {
            throw new BadRequestException("Theme already exists: " + clean);
        }
        return themeRepository.save(Theme.builder().name(clean).build());
    }

    @Transactional
    public Theme rename(Long id, String name) {
        Theme theme = getById(id);
        String clean = requireName(name);
        if (!clean.equals(theme.getName()) && themeRepository.existsByName(clean)) {
            throw new BadRequestException("Theme already exists: " + clean);
        }
        theme.setName(clean);
        return theme;
    }

    @Transactional
    public void delete(Long id) {
        Theme theme = getById(id);
        if (!theme.getPackages().isEmpty()) {
            throw new BadRequestException("Theme is used by travel packages and cannot be deleted.");
        }
        themeRepository.delete(theme);
    }

    private String requireName(String name) {
        if (name == null || name.isBlank()) {
            throw new BadRequestException("Theme name is required.");
        }
        return name.trim();
    }
}
