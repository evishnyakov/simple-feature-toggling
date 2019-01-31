package com.evishnyakov.toggling.security;

import com.evishnyakov.toggling.Feature;
import com.evishnyakov.toggling.FeatureToggleService;
import com.evishnyakov.toggling.FeatureTogglingException;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

@Aspect
public class FeatureTogglingAspect {

    public final FeatureToggleService featureToggleService;

    public FeatureTogglingAspect(FeatureToggleService featureToggleService) {
        this.featureToggleService = featureToggleService;
    }

    @Before("@annotation(featureEnabled)")
    public void featureEnabled(FeatureEnabled featureEnabled) {
        String featureName = featureEnabled.value();
        Feature feature = this.featureToggleService.findFeature(featureName)
                .orElseThrow(() -> new FeatureTogglingException("Feature " + featureName + " is not fined!"));
        if(!featureToggleService.isEnabled(feature)) {
            throw new FeatureTogglingException("Feature " + featureName + " is disabled!");
        }
    }

}
