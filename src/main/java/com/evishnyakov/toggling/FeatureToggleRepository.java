package com.evishnyakov.toggling;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FeatureToggleRepository extends JpaRepository<FeatureToggle, String> {
}
