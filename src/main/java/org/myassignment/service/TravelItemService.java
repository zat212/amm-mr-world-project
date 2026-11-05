package org.myassignment.service;

import lombok.RequiredArgsConstructor;
import org.myassignment.dto.ItemRequest;
import org.myassignment.entity.TravelItem;
import org.myassignment.exception.BadRequestException;
import org.myassignment.exception.ResourceNotFoundException;
import org.myassignment.repository.TravelItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/** Manage Travel Item Inventory (couple T-shirts, ginseng, golf balls, ...). */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TravelItemService {

    private final TravelItemRepository itemRepository;

    public List<TravelItem> getAll() {
        return itemRepository.findAll();
    }

    public TravelItem getById(Long id) {
        return itemRepository.findById(id).orElseThrow(() -> ResourceNotFoundException.of("Item", id));
    }

    public List<TravelItem> getByCategory(String category) {
        return itemRepository.findByCategory(category);
    }

    public List<TravelItem> search(String keyword) {
        return itemRepository.findByNameContainingIgnoreCase(keyword == null ? "" : keyword.trim());
    }

    public List<TravelItem> getLowStock(int threshold) {
        return itemRepository.findByStockQtyLessThan(threshold);
    }

    @Transactional
    public TravelItem create(ItemRequest request) {
        validate(request);
        return itemRepository.save(TravelItem.builder()
                .name(request.name().trim())
                .category(request.category())
                .price(request.price())
                .stockQty(request.stockQty() == null ? 0 : request.stockQty())
                .build());
    }

    @Transactional
    public TravelItem update(Long id, ItemRequest request) {
        validate(request);
        TravelItem item = getById(id);
        item.setName(request.name().trim());
        item.setCategory(request.category());
        item.setPrice(request.price());
        if (request.stockQty() != null) {
            item.setStockQty(request.stockQty());
        }
        return item;
    }

    /** delta > 0 adds stock, delta < 0 removes stock. Stock can never go below zero. */
    @Transactional
    public TravelItem adjustStock(Long id, int delta) {
        TravelItem item = getById(id);
        int updated = item.getStockQty() + delta;
        if (updated < 0) {
            throw new BadRequestException("Not enough stock. Available: " + item.getStockQty());
        }
        item.setStockQty(updated);
        return item;
    }

    @Transactional
    public void delete(Long id) {
        TravelItem item = getById(id);
        if (!item.getPackageItems().isEmpty()) {
            throw new BadRequestException("This item is part of travel packages. Remove it from them first.");
        }
        itemRepository.delete(item);
    }

    private void validate(ItemRequest r) {
        if (r == null || r.name() == null || r.name().isBlank()) {
            throw new BadRequestException("Item name is required.");
        }
        if (r.price() != null && r.price().compareTo(BigDecimal.ZERO) < 0) {
            throw new BadRequestException("Price must be zero or more.");
        }
        if (r.stockQty() != null && r.stockQty() < 0) {
            throw new BadRequestException("Stock cannot be negative.");
        }
    }
}
