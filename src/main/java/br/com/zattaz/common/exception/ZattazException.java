package br.com.zattaz.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class ZattazException extends RuntimeException {

    public ZattazException(String message) {
        super(message);
    }
}
