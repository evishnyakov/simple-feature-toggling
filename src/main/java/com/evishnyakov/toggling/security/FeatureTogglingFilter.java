package com.evishnyakov.toggling.security;

import com.evishnyakov.toggling.FeatureToggleService;
import com.evishnyakov.toggling.FeatureTogglingException;
import com.evishnyakov.toggling.config.FeatureToggling;
import com.evishnyakov.toggling.config.FeatureToggling.TogglingConfig;
import org.springframework.http.HttpMethod;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

public class FeatureTogglingFilter extends OncePerRequestFilter {

    private final FeatureToggleService featureToggleService;
    private final TogglingConfig togglingConfig;

    public FeatureTogglingFilter(FeatureToggleService featureToggleService, TogglingConfig togglingConfig) {
        this.featureToggleService = featureToggleService;
        this.togglingConfig = togglingConfig;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        if(!isEnabled(request)) {
            throw new FeatureTogglingException();
        }
        filterChain.doFilter(request, response);
    }

    private boolean isEnabled(HttpServletRequest request) {
        Map<RequestMatcher, FeatureToggling.FeatureTogglingConfig> configMap = togglingConfig.getConfigMap();
        return configMap.entrySet().stream().filter(e -> e.getKey().matches(request)).findFirst().map(e -> {
            HttpMethod httpMethod = HttpMethod.resolve(request.getMethod());
            FeatureToggling.FeatureTogglingConfig config = e.getValue();
            if(!config.getMethods().contains(httpMethod)) {
                return true;
            }
            return featureToggleService.isEnabled(config.getFeature());
        }).orElse(true);
    }

}
