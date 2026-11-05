package org.myassignment.controller;


import lombok.RequiredArgsConstructor;
import org.myassignment.dto.*;
import org.myassignment.entity.PackageStatus;
import org.myassignment.entity.TravelPackage;
import org.myassignment.exception.ResourceNotFoundException;
import org.myassignment.service.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CatalogController {

    private final ThemeService themeService;
    private final TierService tierService;
    private final TravelPackageService packageService;
    private final DepartureService departureService;
    private final ResponseMapper mapper;

    @GetMapping("/themes")
    public List<ThemeResponse> themes() {
        return themeService.getAll().stream().map(mapper::theme).toList();
    }

    @GetMapping("/tiers")
    public List<TierResponse> tiers() {
        return tierService.getAll().stream().map(mapper::tier).toList();
    }

    @GetMapping("/packages")
    public PageResponse<PackageSummaryResponse> browse(@RequestParam(defaultValue = "0") int page,
                                                       @RequestParam(defaultValue = "12") int size) {
        var pageable = PageRequest.of(Math.max(page, 0), Math.min(Math.max(size, 1), 50),
                Sort.by("id").descending());
        return PageResponse.of(packageService.browse(pageable).map(mapper::summary));
    }

    @GetMapping("/packages/filter")
    public List<PackageSummaryResponse> filter(@RequestParam(required = false) Long themeId,
                                               @RequestParam(required = false) Long tierId) {
        return packageService.filter(themeId, tierId).stream().map(mapper::summary).toList();
    }

    /** Text search. The voice search sends the recognised text here as ?q= */
    @GetMapping("/packages/search")
    public List<PackageSummaryResponse> search(@RequestParam("q") String keyword) {
        return packageService.search(keyword).stream().map(mapper::summary).toList();
    }

    @GetMapping("/packages/{id}")
    public PackageDetailResponse details(@PathVariable Long id) {
        TravelPackage pkg = packageService.getById(id);
        if (pkg.getStatus() != PackageStatus.ACTIVE) {
            throw ResourceNotFoundException.of("Travel package", id);
        }
        return mapper.detail(pkg, departureService.getBookableByPackage(id));
    }

}

