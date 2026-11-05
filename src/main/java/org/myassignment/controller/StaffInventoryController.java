package org.myassignment.controller;

import lombok.RequiredArgsConstructor;
import org.myassignment.dto.ItemRequest;
import org.myassignment.dto.ItemResponse;
import org.myassignment.dto.StockRequest;
import org.myassignment.entity.TravelItem;
import org.myassignment.service.TravelItemService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/staff/items")
@RequiredArgsConstructor
public class StaffInventoryController {

    private final TravelItemService itemService;
    private final ResponseMapper mapper;

    /** Optional filters: ?category=  ?q=  ?lowStock=5 (items with stock below 5). */
    @GetMapping
    public List<ItemResponse> list(@RequestParam(required = false) String category,
                                   @RequestParam(required = false) String q,
                                   @RequestParam(required = false) Integer lowStock) {
        List<TravelItem> items;
        if (lowStock != null) {
            items = itemService.getLowStock(lowStock);
        } else if (category != null && !category.isBlank()) {
            items = itemService.getByCategory(category);
        } else if (q != null && !q.isBlank()) {
            items = itemService.search(q);
        } else {
            items = itemService.getAll();
        }
        return items.stream().map(mapper::item).toList();
    }

    @GetMapping("/{id}")
    public ItemResponse get(@PathVariable Long id) {
        return mapper.item(itemService.getById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemResponse create(@RequestBody ItemRequest request) {
        return mapper.item(itemService.create(request));
    }

    @PutMapping("/{id}")
    public ItemResponse update(@PathVariable Long id, @RequestBody ItemRequest request) {
        return mapper.item(itemService.update(id, request));
    }

    @PatchMapping("/{id}/stock")
    public ItemResponse adjustStock(@PathVariable Long id, @RequestBody StockRequest request) {
        return mapper.item(itemService.adjustStock(id, request.delta()));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        itemService.delete(id);
    }
}

