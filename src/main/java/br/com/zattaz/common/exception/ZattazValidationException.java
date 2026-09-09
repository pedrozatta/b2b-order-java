package br.com.zattaz.common.exception;

import br.com.zattaz.common.model.ValidationError;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
public class ZattazValidationException extends ZattazException {

    private final List<ValidationError> errors;

    public ZattazValidationException(List<ValidationError> errors) {
        super(errors.stream()
                .map(error -> error.field() + ": " + error.message())
                .collect(Collectors.joining("; ")));
        this.errors = List.copyOf(errors);
    }

    public List<ValidationError> getErrors() {
        return errors;
    }
}
