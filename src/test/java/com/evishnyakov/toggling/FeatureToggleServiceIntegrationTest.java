package com.evishnyakov.toggling;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.HashSet;
import java.util.Map;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {TestApplication.class , TestDataSourceConfig.class})
@Transactional
public class FeatureToggleServiceIntegrationTest {

    @Autowired
    private FeatureToggleRepository repo;

    @PersistenceContext
    private EntityManager em;

    private enum COUNTER implements Feature {
        ONE, TWO {
            @Override
            public boolean isEnabledByDefault() {
                return true;
            }
        }
    }

    private FeatureToggleService service;

    @BeforeEach
    public void init() {
        service = new FeatureToggleService(repo);
        service.setAvailableFeatures(asList(COUNTER.values()));
    }

    @Test
    public void is_enabled_default() {
        assertTrue(service.isEnabled(COUNTER.TWO));
        assertFalse(service.isEnabled(COUNTER.ONE));
    }

    @Test
    public void is_enabled() {
        repo.save(FeatureToggle.builder().name(COUNTER.ONE.name()).enabled(true).build());
        repo.save(FeatureToggle.builder().name(COUNTER.TWO.name()).enabled(false).build());

        em.flush();
        em.clear();

        assertFalse(service.isEnabled(COUNTER.TWO));
        assertTrue(service.isEnabled(COUNTER.ONE));
    }

    @Test
    public void set_enabled() {
        service.setEnabled(COUNTER.ONE, true);
        service.setEnabled(COUNTER.TWO, false);

        em.flush();
        em.clear();

        assertTrue(repo.findById(COUNTER.ONE.name()).get().isEnabled());
        assertFalse(repo.findById(COUNTER.TWO.name()).get().isEnabled());
    }

    @Test
    public void get_available_features() {
        assertEquals(new HashSet<>(asList(COUNTER.values())), service.getAvailableFeatures());
    }

    @Test
    public void find_feature() {
        assertEquals(COUNTER.ONE, service.findFeature("ONE").get());
        assertEquals(COUNTER.TWO, service.findFeature("TWO").get());
    }

    @Test
    public void set_enabled_wrong_feature() {
        assertThrows(
                FeatureTogglingException.class,
                () -> {
                    service = new FeatureToggleService(repo);
                    service.setAvailableFeatures(asList(COUNTER.ONE));
                    service.setEnabled(COUNTER.TWO, true);
                }
        );
    }

    @Test
    public void get_all_values() {
        Map<Feature, Boolean> allValues = service.getAllValues();
        assertEquals(2, allValues.size());
        assertFalse(allValues.get(COUNTER.ONE));
        assertTrue(allValues.get(COUNTER.TWO));
    }


}
