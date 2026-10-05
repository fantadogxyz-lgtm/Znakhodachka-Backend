package com.znahodochka.backend.exceptions;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;

@Getter
@Setter
public class BusinessException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = -2345678976574635223L;
    private BusinessExceptionCode businessExceptionCode;

    public BusinessException(final BusinessExceptionCode businessExceptionCode) {
        super();
        this.businessExceptionCode = businessExceptionCode;
    }

    public BusinessException(final BusinessExceptionCode businessExceptionCode, final Throwable t) {
        super(t);
        this.businessExceptionCode = businessExceptionCode;
    }
}
