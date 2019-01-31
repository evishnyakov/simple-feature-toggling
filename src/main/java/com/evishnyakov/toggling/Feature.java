package com.evishnyakov.toggling;

/**
 * This interface represents a feature and is typically implemented by the feature enum.
 */
public interface Feature {

    String name();

    default boolean isEnabledByDefault() {
        return false;
    }

}
