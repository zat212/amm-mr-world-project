package org.myassignment.service;

import lombok.RequiredArgsConstructor;
import org.myassignment.dto.OptionRequest;
import org.myassignment.entity.CustomizeOption;
import org.myassignment.entity.OptionCategory;
import org.myassignment.entity.TravelPackage;
import org.myassignment.exception.BadRequestException;
import org.myassignment.exception.ResourceNotFoundException;
import org.myassignment.repository.CustomizeOptionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OptionService {

    private final CustomizeOptionRepository optionRepository;
    private final TravelPackageService packageService;

    /** Options available for a package so a customer can choose them when booking. */
    public List<CustomizeOption> getByPackage(Long packageId) {
        packageService.getById(packageId);
        return optionRepository.findByTravelPackageId(packageId);
    }

    public List<CustomizeOption> getByPackageAndCategory(Long packageId, OptionCategory category) {
        packageService.getById(packageId);
        return optionRepository.findByTravelPackageIdAndCategory(packageId, category);
    }

    public CustomizeOption getById(Long id) {
        return optionRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Option", id));
    }

    /** Staff adds an option to one package. */
    @Transactional
    public CustomizeOption add(Long packageId, OptionRequest request) {
        validate(request);
        TravelPackage pkg = packageService.getById(packageId);
        CustomizeOption option = CustomizeOption.builder()
                .category(request.category())
                .name(request.name().trim())
                .extraPrice(request.extraPrice() == null ? BigDecimal.ZERO : request.extraPrice())
                .build();
        pkg.addOption(option);
        return optionRepository.save(option);
    }

    /** Staff edits an existing package option. */
    @Transactional
    public CustomizeOption update(Long optionId, OptionRequest request) {
        validate(request);
        CustomizeOption option = getById(optionId);
        option.setCategory(request.category());
        option.setName(request.name().trim());
        option.setExtraPrice(request.extraPrice() == null ? BigDecimal.ZERO : request.extraPrice());
        return option;
    }

    /** Prevent removing options that are already part of reservations. */
    @Transactional
    public void delete(Long optionId) {
        CustomizeOption option = getById(optionId);
        if (!option.getReservationOptions().isEmpty()) {
            throw new BadRequestException("This option is already used in reservations and cannot be deleted.");
        }
        option.getTravelPackage().getOptions().remove(option);
        optionRepository.delete(option);
    }

    private void validate(OptionRequest request) {
        if (request == null || request.category() == null) {
            throw new BadRequestException("Option category is required.");
        }
        if (request.name() == null || request.name().isBlank()) {
            throw new BadRequestException("Option name is required.");
        }
        if (request.extraPrice() != null && request.extraPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new BadRequestException("Extra price must be zero or more.");
        }
    }
}
