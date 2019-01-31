package com.evishnyakov.toggling.config;

import com.evishnyakov.toggling.FeatureToggleRepository;
import com.evishnyakov.toggling.FeatureToggleService;
import com.evishnyakov.toggling.security.FeatureTogglingAspect;
import com.evishnyakov.toggling.security.FeatureTogglingFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;

public abstract class FeatureTogglingConfigurerAdapter {

    @Bean
    public FeatureToggleService featureToggleService(FeatureToggleRepository repository) {
        return new FeatureToggleService(repository);
    }

    @Bean
    public FeatureTogglingAspect featureTogglingAspect(FeatureToggleService featureToggleService) {
        return new FeatureTogglingAspect(featureToggleService);
    }

    @Bean
    public FilterRegistrationBean featureTogglingFilter(FeatureToggleService featureToggleService) {
        FeatureToggling featureToggling = new FeatureToggling();
        configure(featureToggling);
        FeatureToggling.TogglingConfig togglingConfig = featureToggling.getTogglingConfig();

        featureToggleService.setAvailableFeatures(togglingConfig.getAllFeatures());

        FilterRegistrationBean bean = new FilterRegistrationBean();
        bean.setFilter(new FeatureTogglingFilter(featureToggleService, togglingConfig));
        bean.setOrder(Ordered.LOWEST_PRECEDENCE);
        return bean;
    }

    public abstract void configure(FeatureToggling featureToggling);

}
