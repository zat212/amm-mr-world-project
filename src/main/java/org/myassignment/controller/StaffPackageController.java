package org.myassignment.controller;


import lombok.RequiredArgsConstructor;
import org.myassignment.dto.*;
import org.springframework.security.core.Authentication;
import org.myassignment.service.DepartureService;
import org.myassignment.service.PackageItemService;
import org.myassignment.service.TravelPackageService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/staff")
@RequiredArgsConstructor
public class StaffPackageController {

    private final TravelPackageService packageService;
    private final DepartureService departureService;
    private final PackageItemService packageItemService;
    private final CurrentUserResolver currentUser;
    private final ResponseMapper mapper;



    @GetMapping("/packages")
    public List<PackageSummaryResponse> all() {
        return packageService.getAll().stream().map(mapper::summary).toList();
    }

    @GetMapping("/packages/{id}")
    public PackageDetailResponse details(@PathVariable Long id) {
        var pkg = packageService.getById(id);
        return mapper.detail(pkg, departureService.getByPackage(id));
    }

    @PostMapping("/packages")
    @ResponseStatus(HttpStatus.CREATED)
    public PackageSummaryResponse create(Authentication auth, @RequestBody PackageRequest request) {
        var staff = currentUser.get(auth);
        return mapper.summary(packageService.create(staff.getId(), request));
    }

    @PutMapping("/packages/{id}")
    public PackageSummaryResponse update(@PathVariable Long id, @RequestBody PackageRequest request) {
        return mapper.summary(packageService.update(id, request));
    }

    @DeleteMapping("/packages/{id}")
    public PackageSummaryResponse delete(@PathVariable Long id) {
        return mapper.summary(packageService.deactivate(id));
    }

    @PatchMapping("/packages/{id}/activate")
    public PackageSummaryResponse activate(@PathVariable Long id) {
        return mapper.summary(packageService.activate(id));
    }

    @GetMapping("/packages/{id}/departures")
    public List<DepartureResponse> departures(@PathVariable Long id) {
        return departureService.getByPackage(id).stream().map(mapper::departure).toList();
    }

    @PostMapping("/packages/{id}/departures")
    @ResponseStatus(HttpStatus.CREATED)
    public DepartureResponse addDeparture(@PathVariable Long id, @RequestBody DepartureRequest request) {
        return mapper.departure(departureService.create(id, request));
    }

    @PatchMapping("/departures/{departureId}/cancel")
    public DepartureResponse cancelDeparture(@PathVariable Long departureId) {
        return mapper.departure(departureService.cancel(departureId));
    }

    @PostMapping("/packages/{id}/items")
    @ResponseStatus(HttpStatus.CREATED)
    public PackageItemResponse addItem(@PathVariable Long id, @RequestBody PackageItemRequest request) {
        int qty = request.quantity() == null ? 1 : request.quantity();
        return mapper.packageItem(packageItemService.addItem(id, request.itemId(), qty));
    }

    @PutMapping("/packages/{id}/items/{itemId}")
    public PackageItemResponse updateItemQuantity(@PathVariable Long id, @PathVariable Long itemId,
                                                  @RequestBody QuantityRequest request) {
        int qty = request.quantity() == null ? 1 : request.quantity();
        return mapper.packageItem(packageItemService.updateQuantity(id, itemId, qty));
    }

    @DeleteMapping("/packages/{id}/items/{itemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeItem(@PathVariable Long id, @PathVariable Long itemId) {
        packageItemService.removeItem(id, itemId);
    }

}
