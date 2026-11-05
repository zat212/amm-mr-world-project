package org.myassignment.service;

import lombok.RequiredArgsConstructor;
import org.myassignment.dto.PackageRequest;
import org.myassignment.entity.PackageStatus;
import org.myassignment.entity.Theme;
import org.myassignment.entity.Tier;
import org.myassignment.entity.TravelPackage;
import org.myassignment.entity.User;
import org.myassignment.exception.BadRequestException;
import org.myassignment.exception.ResourceNotFoundException;
import org.myassignment.repository.TravelPackageRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TravelPackageService {

    private final TravelPackageRepository packageRepository;
    private final ThemeService themeService;
    private final TierService tierService;
    private final UserService userService;

    // ---------- Customer: browse / search ----------

    public Page<TravelPackage> browse(Pageable pageable) {
        return packageRepository.findByStatus(PackageStatus.ACTIVE, pageable);
    }

    /** Select Theme & Tier. Pass null to ignore a filter. */
    public List<TravelPackage> filter(Long themeId, Long tierId) {
        if (themeId == null) {
            return packageRepository.findByStatus(PackageStatus.ACTIVE, Pageable.unpaged()).getContent();
        }
        if (tierId == null) {
            return packageRepository.findByThemeIdAndStatus(themeId, PackageStatus.ACTIVE);
        }
        return packageRepository.findByThemeIdAndTierIdAndStatus(themeId, tierId, PackageStatus.ACTIVE);
    }

    /** Text search. Voice search (STT) just sends the recognised text here. */
    public List<TravelPackage> search(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return filter(null, null);
        }
        return packageRepository.findByStatusAndTitleContainingIgnoreCase(PackageStatus.ACTIVE, keyword.trim());
    }

    public TravelPackage getById(Long id) {
        return packageRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Travel package", id));
    }

    // ---------- Staff: register / edit / delete ----------

    /** Staff view: every package regardless of status. */
    public List<TravelPackage> getAll() {
        return packageRepository.findAll();
    }

    @Transactional
    public TravelPackage create(Long staffId, PackageRequest request) {
        validate(request);
        Theme theme = themeService.getById(request.themeId());
        Tier tier = tierService.getById(request.tierId());
        User staff = userService.getById(staffId);

        TravelPackage pkg = TravelPackage.builder()
                .theme(theme)
                .tier(tier)
                .createdBy(staff)
                .title(request.title().trim())
                .description(request.description())
                .basePrice(request.basePrice())
                .durationDays(request.durationDays())
                .build();
        if (request.status() != null) {
            pkg.setStatus(request.status());
        }
        return packageRepository.save(pkg);
    }

    @Transactional
    public TravelPackage update(Long id, PackageRequest request) {
        validate(request);
        TravelPackage pkg = getById(id);
        pkg.setTheme(themeService.getById(request.themeId()));
        pkg.setTier(tierService.getById(request.tierId()));
        pkg.setTitle(request.title().trim());
        pkg.setDescription(request.description());
        pkg.setBasePrice(request.basePrice());
        pkg.setDurationDays(request.durationDays());
        if (request.status() != null) {
            pkg.setStatus(request.status());
        }
        return pkg;
    }

    /**
     * "Delete" is a soft delete: the package becomes INACTIVE, so old reservations
     * and travel history that point to it stay valid.
     */
    @Transactional
    public TravelPackage deactivate(Long id) {
        TravelPackage pkg = getById(id);
        pkg.setStatus(PackageStatus.INACTIVE);
        return pkg;
    }

    @Transactional
    public TravelPackage activate(Long id) {
        TravelPackage pkg = getById(id);
        pkg.setStatus(PackageStatus.ACTIVE);
        return pkg;
    }

    private void validate(PackageRequest r) {
        if (r == null) {
            throw new BadRequestException("Request body is required.");
        }
        if (r.themeId() == null || r.tierId() == null) {
            throw new BadRequestException("Theme and tier are required.");
        }
        if (r.title() == null || r.title().isBlank()) {
            throw new BadRequestException("Title is required.");
        }
        if (r.basePrice() == null || r.basePrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new BadRequestException("Base price must be zero or more.");
        }
        if (r.durationDays() != null && r.durationDays() < 1) {
            throw new BadRequestException("Duration must be at least 1 day.");
        }
    }
}
