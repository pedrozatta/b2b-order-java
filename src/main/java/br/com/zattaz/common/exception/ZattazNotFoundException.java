package br.com.zattaz.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ZattazNotFoundException extends ZattazBusinessException {

    public ZattazNotFoundException(String message) {
        super(message);
    }
}
