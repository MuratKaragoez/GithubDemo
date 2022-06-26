package com.murat.demogithub.controller.advice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.*;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@ControllerAdvice
public class GithubRepoControllerAdvice extends ResponseEntityExceptionHandler {

    public static class GithubRepoAPINotAvailable extends RuntimeException {
        public GithubRepoAPINotAvailable() {
            super();
        }
    }

    public static class GithubRepoAPIRateLimitExceeded extends RuntimeException {
        public GithubRepoAPIRateLimitExceeded() {
            super();
        }
    }

    public static class GithubRepoAPIUnprocessableEntity extends RuntimeException {
        public GithubRepoAPIUnprocessableEntity(String reason) {
            super(reason);
        }
    }

    public static class GithubRepoAPIUnauthorized extends RuntimeException {
        public GithubRepoAPIUnauthorized(String reason) {
            super(reason);
        }
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(GithubRepoAPIUnauthorized.class)
    protected ClientErrorInformation handleConflict(GithubRepoAPIUnauthorized ex, WebRequest request) throws JsonProcessingException {
        Map<String, Object> details = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();
        details.put("github", mapper.readTree(ex.getMessage()));
        return new ClientErrorInformation("The Github API threw an error. Make sure your access token is valid.", details);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ExceptionHandler(GithubRepoAPIUnprocessableEntity.class)
    protected ClientErrorInformation handleConflict(GithubRepoAPIUnprocessableEntity ex, WebRequest request) throws JsonProcessingException {
        Map<String, Object> details = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();
        details.put("github", mapper.readTree(ex.getMessage()));
        return new ClientErrorInformation("The Github API threw an error.", details);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_GATEWAY)
    @ExceptionHandler(GithubRepoAPINotAvailable.class)
    protected ClientErrorInformation handleConflict(GithubRepoAPINotAvailable ex, WebRequest request) {
        return new ClientErrorInformation("The Github API is currently not reachable.");
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(GithubRepoAPIRateLimitExceeded.class)
    protected ClientErrorInformation handleConflict(GithubRepoAPIRateLimitExceeded ex, WebRequest request) {
        return new ClientErrorInformation("The Github API rate limit is exceeded.");
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(ConstraintViolationException.class)
    protected ClientErrorInformation handleConflict(ConstraintViolationException ex, WebRequest request) {
        Map<String, Object> details = new HashMap<>();
        ex.getConstraintViolations().forEach(violation -> {
            String path = violation.getPropertyPath().toString();
            details.put(path.substring(path.lastIndexOf(".") + 1), violation.getMessage());
        });
        return new ClientErrorInformation("Invalid Request.", details);
    }

    @Target({PARAMETER})
    @Retention(RUNTIME)
    @Constraint(validatedBy = {ValidDateValidator.class})
    public @interface ValidDate {
        String message() default "The date has to be in the format yyy-MM-dd";

        Class<?>[] groups() default {};

        Class<? extends Payload>[] payload() default {};
    }

    public static class ValidDateValidator implements ConstraintValidator<ValidDate, String> {

        @Override
        public boolean isValid(String value, ConstraintValidatorContext context) {
            if (value == null) {
                return true;
            }
            try {
                DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH).parse(value);
                return true;
            } catch (DateTimeParseException e) {
                return false;
            }
        }
    }
}
