package org.myassignment.repository;

import org.myassignment.entity.CustomizeOption;
import org.myassignment.entity.OptionCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CustomizeOptionRepository extends JpaRepository<CustomizeOption, Long> {

    List<CustomizeOption> findByTravelPackageId(Long packageId);

    // Hotel / Transportation / Dining
    List<CustomizeOption> findByTravelPackageIdAndCategory(Long packageId, OptionCategory category);
}
