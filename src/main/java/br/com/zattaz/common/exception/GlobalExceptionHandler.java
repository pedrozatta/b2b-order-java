package br.com.zattaz.common.exception;

import br.com.zattaz.common.model.ValidationError;
import br.com.zattaz.common.trace.TraceId;
import jakarta.persistence.OptimisticLockException;
import jakarta.validation.ConstraintViolationException;
import java.time.ZonedDateTime;
import java.util.List;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class GlobalExceptionHandler {

    @ExceptionHandler(ZattazValidationException.class)
    public ProblemDetail handleZattazValidationException(ZattazValidationException ex) {
        ProblemDetail problem = buildProblem(ex);
        problem.setProperty("details", ex.getErrors());
        return problem;
    }

    @ExceptionHandler(ZattazException.class)
    public ProblemDetail handleZattazException(ZattazException ex) {
        return buildProblem(ex);
    }

    @ExceptionHandler({OptimisticLockingFailureException.class, OptimisticLockException.class})
    public ProblemDetail handleOptimisticLock(RuntimeException ex) {
        return buildProblem(new ZattazConflictException("Concurrent update conflict"));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail handleIllegalArgument(IllegalArgumentException ex) {
        return buildProblem(new ZattazBusinessException(ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        List<ValidationError> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> new ValidationError(error.getField(), error.getDefaultMessage()))
                .toList();
        return buildValidationProblem(errors);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ProblemDetail handleConstraintViolation(ConstraintViolationException ex) {
        List<ValidationError> errors = ex.getConstraintViolations().stream()
                .map(v -> {
                    String path = v.getPropertyPath().toString();
                    String field = path.contains(".") ? path.substring(path.lastIndexOf('.') + 1) : path;
                    return new ValidationError(field, v.getMessage());
                })
                .toList();
        return buildValidationProblem(errors);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ProblemDetail handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        String detail = ex.getMostSpecificCause().getMessage();
        if (detail == null || detail.isBlank()) {
            detail = HttpStatus.BAD_REQUEST.getReasonPhrase();
        }
        return buildProblem(new ZattazBusinessException(detail));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ProblemDetail handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex) {
        return buildProblem(ex);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ProblemDetail handleMissingServletRequestParameter(MissingServletRequestParameterException ex) {
        return buildProblem(ex);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ProblemDetail handleMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.METHOD_NOT_ALLOWED,
                ex.getMessage() != null ? ex.getMessage() : HttpStatus.METHOD_NOT_ALLOWED.getReasonPhrase());
        enrich(problem);
        return problem;
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ProblemDetail handleNoResourceFound(NoResourceFoundException ex) {
        return buildProblem(ex);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ProblemDetail handleAuthenticationException(AuthenticationException ex) {
        ProblemDetail problem =
                ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        enrich(problem);
        return problem;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleUnexpected(Exception ex) {
        return buildProblem(new ZattazException("Unexpected error occurred"));
    }

    private ProblemDetail buildValidationProblem(List<ValidationError> errors) {
        ProblemDetail problem = buildProblem(new ZattazValidationException(errors));
        problem.setProperty("details", errors);
        return problem;
    }

    private ProblemDetail buildProblem(Throwable ex) {
        ResponseStatus responseStatus =
                AnnotationUtils.findAnnotation(ex.getClass(), ResponseStatus.class);

        HttpStatus status = responseStatus != null
                ? responseStatus.value()
                : HttpStatus.INTERNAL_SERVER_ERROR;

        String detail = ex.getMessage() != null ? ex.getMessage() : status.getReasonPhrase();
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, detail);
        enrich(problem);
        return problem;
    }

    private void enrich(ProblemDetail problem) {
        String traceId = currentTraceId();
        if (traceId != null) {
            problem.setProperty("traceId", traceId);
            problem.setProperty("date", ZonedDateTime.now());
        }
    }

    private String currentTraceId() {
        return MDC.get(TraceId.KEY);
    }
}
