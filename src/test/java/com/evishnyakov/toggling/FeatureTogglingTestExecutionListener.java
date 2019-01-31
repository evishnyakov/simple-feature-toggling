package com.evishnyakov.toggling;

import org.springframework.core.Conventions;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;

public class FeatureTogglingTestExecutionListener extends AbstractTestExecutionListener {

    public static final String RESET_FEATURE_TOGGLE_CONTEXT_ATTRIBUTE = Conventions.getQualifiedAttributeName(
            FeatureTogglingTestExecutionListener.class, "resetFeatureToggleContext");

    @Override
    public void beforeTestMethod(TestContext testContext) {
        WithFeature annotation = AnnotationUtils.getAnnotation(testContext.getTestMethod(), WithFeature.class);
        if(annotation == null) {
            return;
        }
        FeatureToggleService featureToggleService = testContext.getApplicationContext().getBean(FeatureToggleService.class);
        Feature feature = featureToggleService.findFeature(annotation.value()).orElseThrow(
                () -> new RuntimeException("There is not feture with name: " + annotation.value())
        );
        if(!featureToggleService.isEnabled(feature)) {
            testContext.setAttribute(RESET_FEATURE_TOGGLE_CONTEXT_ATTRIBUTE, feature);
            featureToggleService.setEnabled(feature, true);
        }
    }

    @Override
    public void afterTestMethod(TestContext testContext) {
        Feature feature = (Feature) testContext.getAttribute(RESET_FEATURE_TOGGLE_CONTEXT_ATTRIBUTE);
        if(feature != null) {
            FeatureToggleService featureToggleService = testContext.getApplicationContext().getBean(FeatureToggleService.class);
            featureToggleService.setEnabled(feature, false);
        }
    }
}
