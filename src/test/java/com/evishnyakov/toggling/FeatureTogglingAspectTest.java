package com.evishnyakov.toggling;

import com.evishnyakov.toggling.config.FeatureToggling;
import com.evishnyakov.toggling.config.FeatureTogglingConfigurerAdapter;
import com.evishnyakov.toggling.security.FeatureEnabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {TestApplication.class , TestDataSourceConfig.class, FeatureTogglingAspectTest.FeatureTogglingConfig.class})
@Transactional
public class FeatureTogglingAspectTest {

    @MockBean
    private FeatureToggleRepository featureToggleRepository;

    @Autowired
    private TestService testService;

    public enum TestFeature implements Feature {
        TEST
    }

    @Test
    public void checkEnabled() {
        when(featureToggleRepository.findById(TestFeature.TEST.name()))
                .thenReturn(Optional.of(FeatureToggle.builder().enabled(true).build()));

        assertThat(testService.secureMethod()).isEqualTo("OK");
    }

    @Test
    public void checkDisabled() {
        when(featureToggleRepository.findById(TestFeature.TEST.name()))
                .thenReturn(Optional.of(FeatureToggle.builder().enabled(false).build()));

        assertThrows(FeatureTogglingException.class, () -> testService.secureMethod());
    }

    public static class FeatureTogglingConfig extends FeatureTogglingConfigurerAdapter {
        @Override
        public void configure(FeatureToggling featureToggling) {
            featureToggling.register(TestFeature.TEST);
        }
        @Bean
        public TestService testService() {
            return new TestService();
        }
    }

    public static class TestService {
        @FeatureEnabled("TEST")
        public String secureMethod() {
            return "OK";
        }
    }

}
