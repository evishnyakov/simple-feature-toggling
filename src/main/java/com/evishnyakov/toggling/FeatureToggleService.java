package com.evishnyakov.toggling;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Collections.unmodifiableSet;

/**
 * Manages state of the features.
 */
public class FeatureToggleService {

    private final Set<Feature> features = new LinkedHashSet<>();

    private final JpaRepository<FeatureToggle, String> repository;

    public FeatureToggleService(JpaRepository<FeatureToggle, String> repository) {
        this.repository = repository;
    }

    public void setAvailableFeatures(Collection<Feature> features) {
        this.features.addAll(features);
    }

    public boolean isEnabled(Feature feature) {
        if(feature == null || !this.features.contains(feature)) {
            return false;
        }
        Optional<FeatureToggle> featureToggle = repository.findById(feature.name());
        return featureToggle.map(FeatureToggle::isEnabled).orElseGet(feature::isEnabledByDefault);
    }

    public void setEnabled(Feature feature, boolean enabled) {
        if(!this.features.contains(feature)) {
            throw new FeatureTogglingException("Feature " + feature.name() + " is not allowed!");
        }
        FeatureToggle featureToggle = FeatureToggle.builder().name(feature.name()).enabled(enabled).build();
        repository.save(featureToggle);
    }

    public Set<Feature> getAvailableFeatures() {
        return unmodifiableSet(this.features);
    }

    public Optional<Feature> findFeature(String name) {
        return this.features.stream().filter(f -> f.name().equals(name)).findFirst();
    }

    public Map<Feature, Boolean> getAllValues() {
        List<FeatureToggle> featureToggles = repository.findAll();
        Map<String, Boolean> feature2enabled = featureToggles.stream()
                .collect(Collectors.toMap(FeatureToggle::getName, FeatureToggle::isEnabled));
        Map<Feature, Boolean> map = new LinkedHashMap<>();
        this.features.forEach(f ->
            map.put(f, feature2enabled.getOrDefault(f.name(), f.isEnabledByDefault()))
        );
        return map;
    }


}
