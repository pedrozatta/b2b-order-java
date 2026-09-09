package br.com.zattaz.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class ZattazConflictException extends ZattazBusinessException {

    public ZattazConflictException(String message) {
        super(message);
    }
}
