package com.evishnyakov.toggling;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "feature_toggle")
@EqualsAndHashCode
public class FeatureToggle {

    /**
     * Feature name
     */
    @Id
    private String name;

    /**
     * State of the feature
     */
    private boolean enabled;

}
