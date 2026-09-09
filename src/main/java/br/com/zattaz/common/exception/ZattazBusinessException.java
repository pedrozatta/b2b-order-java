package br.com.zattaz.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ZattazBusinessException extends ZattazException {

    public ZattazBusinessException(String message) {
        super(message);
    }
}
