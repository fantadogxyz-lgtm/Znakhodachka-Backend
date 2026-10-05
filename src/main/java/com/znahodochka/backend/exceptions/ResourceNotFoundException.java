package com.znahodochka.backend.exceptions;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Slf4j
@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends BusinessException {

    public ResourceNotFoundException() {
        super(BusinessExceptionCode.RESOURCE_NOT_FOUND_EXCEPTION);
    }

    public ResourceNotFoundException(final String message) {
        this();
        if (log.isErrorEnabled()) {
            log.error("Resource not found: {}", message);
        }
    }

    public ResourceNotFoundException(final Throwable t) {
        super(BusinessExceptionCode.RESOURCE_NOT_FOUND_EXCEPTION, t);
    }
}
