package com.znahodochka.backend.exceptions;


import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;

@Setter
@Getter
@ResponseStatus(HttpStatus.CONFLICT)
public class BadRequestException extends BusinessException {

    @Serial
    private static final long serialVersionUID = -1181189959035548687L;

    public BadRequestException(final BusinessExceptionCode businessExceptionCode) {
        super(businessExceptionCode);
    }

    public BadRequestException(final BusinessExceptionCode businessExceptionCode, final Throwable t) {
        super(businessExceptionCode, t);
    }
}
