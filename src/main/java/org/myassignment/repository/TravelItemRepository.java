package org.myassignment.repository;

import org.myassignment.entity.TravelItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TravelItemRepository extends JpaRepository<TravelItem, Long> {

    List<TravelItem> findByCategory(String category);

    List<TravelItem> findByNameContainingIgnoreCase(String keyword);

    // Low-stock warning for staff
    List<TravelItem> findByStockQtyLessThan(Integer threshold);
}
