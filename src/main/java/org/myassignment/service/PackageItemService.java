package org.myassignment.service;

import lombok.RequiredArgsConstructor;
import org.myassignment.entity.PackageItem;
import org.myassignment.entity.PackageItemId;
import org.myassignment.entity.TravelItem;
import org.myassignment.entity.TravelPackage;
import org.myassignment.exception.BadRequestException;
import org.myassignment.exception.ResourceNotFoundException;
import org.myassignment.repository.PackageItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** Links inventory items (e.g. couple T-shirts) to the packages that include them. */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PackageItemService {

    private final PackageItemRepository packageItemRepository;
    private final TravelPackageService packageService;
    private final TravelItemService itemService;

    public List<PackageItem> getByPackage(Long packageId) {
        return packageItemRepository.findByTravelPackageId(packageId);
    }

    @Transactional
    public PackageItem addItem(Long packageId, Long itemId, int quantity) {
        if (quantity < 1) {
            throw new BadRequestException("Quantity must be at least 1.");
        }
        PackageItemId id = new PackageItemId(packageId, itemId);
        if (packageItemRepository.existsById(id)) {
            throw new BadRequestException("This item is already part of the package.");
        }
        TravelPackage pkg = packageService.getById(packageId);
        TravelItem item = itemService.getById(itemId);

        return packageItemRepository.save(PackageItem.builder()
                .id(id)
                .travelPackage(pkg)
                .item(item)
                .quantity(quantity)
                .build());
    }

    @Transactional
    public PackageItem updateQuantity(Long packageId, Long itemId, int quantity) {
        if (quantity < 1) {
            throw new BadRequestException("Quantity must be at least 1.");
        }
        PackageItem packageItem = packageItemRepository.findById(new PackageItemId(packageId, itemId))
                .orElseThrow(() -> new ResourceNotFoundException("Item is not part of this package."));
        packageItem.setQuantity(quantity);
        return packageItem;
    }

    @Transactional
    public void removeItem(Long packageId, Long itemId) {
        PackageItemId id = new PackageItemId(packageId, itemId);
        if (!packageItemRepository.existsById(id)) {
            throw new ResourceNotFoundException("Item is not part of this package.");
        }
        packageItemRepository.deleteById(id);
    }
}
