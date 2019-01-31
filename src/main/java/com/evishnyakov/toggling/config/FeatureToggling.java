package com.evishnyakov.toggling.config;

import com.evishnyakov.toggling.Feature;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpMethod;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RegexRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FeatureToggling {

    private TogglingConfig togglingConfig = new TogglingConfig();

    public TogglingConfig getTogglingConfig() {
        return togglingConfig;
    }

    public PathFeatureToggling antMatcher(String path) {
        return new PathFeatureToggling(this, new AntPathRequestMatcher(path));
    }

    public PathFeatureToggling regexMatcher(String pattern) {
        return new PathFeatureToggling(this, new RegexRequestMatcher(pattern, null));
    }

    public FeatureToggling register(Feature feature) {
        togglingConfig.register(feature);
        return this;
    }

    @AllArgsConstructor
    public static class PathFeatureToggling {

        private final FeatureTogglingConfig featureTogglingConfig = new FeatureTogglingConfig();

        private final FeatureToggling featureToggling;
        private final RequestMatcher requestMatcher;


        public PathFeatureToggling anyModification() {
            featureTogglingConfig.getMethods().addAll(
                    Stream.of(HttpMethod.values()).filter(m -> m != HttpMethod.GET).collect(Collectors.toList()));
            return this;
        }

        public PathFeatureToggling get() {
            featureTogglingConfig.getMethods().add(HttpMethod.GET);
            return this;
        }

        public PathFeatureToggling put() {
            featureTogglingConfig.getMethods().add(HttpMethod.PUT);
            return this;
        }

        public PathFeatureToggling delete() {
            featureTogglingConfig.getMethods().add(HttpMethod.DELETE);
            return this;
        }

        public PathFeatureToggling patch() {
            featureTogglingConfig.getMethods().add(HttpMethod.PATCH);
            return this;
        }

        public PathFeatureToggling post() {
            featureTogglingConfig.getMethods().add(HttpMethod.POST);
            return this;
        }

        public PathFeatureToggling methods(HttpMethod method) {
            if(method != null) {
                featureTogglingConfig.getMethods().add(method);
            }
            return this;
        }

        public FeatureToggling withFeature(Feature feature) {
            featureTogglingConfig.setFeature(feature);
            featureToggling.getTogglingConfig().getConfigMap().put(requestMatcher, featureTogglingConfig);

            return featureToggling;
        }

    }

    @Getter
    public static class TogglingConfig {

        private Map<Feature, Instant> feature2disableDate = new HashMap<>();
        private Map<Feature, Instant> feature2enableDate  = new HashMap<>();

        private Set<Feature> features = new LinkedHashSet<>();

        private Map<RequestMatcher, FeatureTogglingConfig> configMap = new LinkedHashMap<>();

        public Collection<Feature> getAllFeatures() {
            Set<Feature> result = new LinkedHashSet<>();
            result.addAll(features);
            result.addAll(configMap.values().stream()
                    .map(FeatureTogglingConfig::getFeature).collect(Collectors.toSet()));
            return result;
        }

        public void register(Feature feature) {
            Objects.requireNonNull(feature);
            features.add(feature);
        }

    }

    @Getter
    public static class FeatureTogglingConfig {

        @Setter
        private Feature feature;

        private Set<HttpMethod> methods = new HashSet<>();

    }


}
