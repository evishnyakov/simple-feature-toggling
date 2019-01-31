package com.evishnyakov.toggling;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
public class FeatureTogglingException extends RuntimeException {

    public FeatureTogglingException() {
        super();
    }

    public FeatureTogglingException(String message) {
        super(message);
    }

}
