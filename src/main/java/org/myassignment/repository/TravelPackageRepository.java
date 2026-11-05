package org.myassignment.repository;

import org.myassignment.entity.PackageStatus;
import org.myassignment.entity.TravelPackage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TravelPackageRepository extends JpaRepository<TravelPackage, Long> {

    // Browse packages (loads theme + tier in one query to avoid lazy-loading errors)
    @EntityGraph(attributePaths = {"theme", "tier"})
    Page<TravelPackage> findByStatus(PackageStatus status, Pageable pageable);

    // Select Theme & Tier
    @EntityGraph(attributePaths = {"theme", "tier"})
    List<TravelPackage> findByThemeIdAndStatus(Long themeId, PackageStatus status);

    @EntityGraph(attributePaths = {"theme", "tier"})
    List<TravelPackage> findByThemeIdAndTierIdAndStatus(Long themeId, Long tierId, PackageStatus status);

    // Search (also used by voice search after speech-to-text)
    @EntityGraph(attributePaths = {"theme", "tier"})
    List<TravelPackage> findByStatusAndTitleContainingIgnoreCase(PackageStatus status, String keyword);
}
