package com.evishnyakov.toggling;

import com.evishnyakov.toggling.config.FeatureToggling;
import com.evishnyakov.toggling.config.FeatureTogglingConfigurerAdapter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@Import(FeatureTogglingControllerTest.FeatureTogglingConfig.class)
@WebMvcTest(controllers = FeatureTogglingControllerTest.TestController.class, secure = false)
public class FeatureTogglingControllerTest {

    private static final String PATH = "/api/v1/tests";

    @MockBean
    private FeatureToggleRepository featureToggleRepository;

    @Autowired
    private MockMvc mockMvc;

    public enum TestFeature implements Feature {
        TEST
    }

    public static class FeatureTogglingConfig extends FeatureTogglingConfigurerAdapter {

        @Override
        public void configure(FeatureToggling featureToggling) {
            featureToggling
                    .antMatcher(PATH + "/**")
                    .get()
                    .withFeature(TestFeature.TEST);
        }

        @Bean
        public TestController testController() {
            return new TestController();
        }

    }

    @RestController
    @RequestMapping(PATH)
    public static class TestController {

        @GetMapping("/{id}")
        public String getOne(@PathVariable("id") int id) {
            return "OK";
        }

        @GetMapping
        public String getAll() {
            return "OK";
        }

    }

    @Test
    public void get_one_exception() {
        assertThrows(
                FeatureTogglingException.class,
                () -> this.mockMvc.perform(get(PATH+ "/100")).andExpect(status().isOk()));
    }

    @Test
    public void get_all_exception() {
        assertThrows(
                FeatureTogglingException.class,
                () -> this.mockMvc.perform(get(PATH)).andExpect(status().isOk()));
    }

    @Test
    public void get_one() throws Exception {
        when(featureToggleRepository.findById(TestFeature.TEST.name()))
                .thenReturn(Optional.of(FeatureToggle.builder().enabled(true).build()));

        this.mockMvc.perform(get(PATH+ "/100")).andExpect(status().isOk());
    }

    @Test
    public void get_all() throws Exception {
        when(featureToggleRepository.findById(TestFeature.TEST.name()))
                .thenReturn(Optional.of(FeatureToggle.builder().enabled(true).build()));

        this.mockMvc.perform(get(PATH)).andExpect(status().isOk());
    }

}
