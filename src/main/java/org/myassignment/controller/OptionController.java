package org.myassignment.controller;

import lombok.RequiredArgsConstructor;
import org.myassignment.dto.OptionRequest;
import org.myassignment.dto.OptionResponse;
import org.myassignment.entity.OptionCategory;
import org.myassignment.service.OptionService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class OptionController {

    private final OptionService optionService;
    private final ResponseMapper mapper;

    /** Public catalog endpoint: users can view all options or filter by category. */
    @GetMapping("/packages/{packageId}/options")
    public List<OptionResponse> getPackageOptions(@PathVariable Long packageId,
                                                  @RequestParam(required = false) OptionCategory category) {
        var options = category == null
                ? optionService.getByPackage(packageId)
                : optionService.getByPackageAndCategory(packageId, category);
        return options.stream().map(mapper::option).toList();
    }

    /** Staff endpoint: add an option to a package. */
    @PostMapping("/staff/packages/{packageId}/options")
    @ResponseStatus(HttpStatus.CREATED)
    public OptionResponse add(@PathVariable Long packageId, @RequestBody OptionRequest request) {
        return mapper.option(optionService.add(packageId, request));
    }

    /** Staff endpoint: update an option. */
    @PutMapping("/staff/options/{optionId}")
    public OptionResponse update(@PathVariable Long optionId, @RequestBody OptionRequest request) {
        return mapper.option(optionService.update(optionId, request));
    }

    /** Staff endpoint: remove an unused option. */
    @DeleteMapping("/staff/options/{optionId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long optionId) {
        optionService.delete(optionId);
    }
}
