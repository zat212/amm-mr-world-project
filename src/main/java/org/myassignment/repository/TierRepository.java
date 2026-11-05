package org.myassignment.repository;

import org.myassignment.entity.Tier;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TierRepository extends JpaRepository<Tier, Long> {

    Optional<Tier> findByName(String name);

    boolean existsByName(String name);
}
