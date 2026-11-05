package org.myassignment.repository;

import org.myassignment.entity.PackageItem;
import org.myassignment.entity.PackageItemId;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PackageItemRepository extends JpaRepository<PackageItem, PackageItemId> {

    @EntityGraph(attributePaths = {"item"})
    List<PackageItem> findByTravelPackageId(Long packageId);

    List<PackageItem> findByItemId(Long itemId);
}
