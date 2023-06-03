package isthatkirill.handler;

import isthatkirill.exceptions.EntityNotFoundException;
import isthatkirill.exceptions.OnUpdateException;
import isthatkirill.exceptions.UserFeedFieldNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;
import java.sql.SQLException;

@Slf4j
@RestControllerAdvice("ru.yandex.practicum.filmorate")
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse entityNotFoundHandle(final EntityNotFoundException e) {
        log.warn(e.getMessage());
        return new ErrorResponse("Not found", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse invalidUpdateHandle(final OnUpdateException e) {
        log.warn("{}: {}", e.getClass().getSimpleName(), e.getMessage());
        return new ErrorResponse("Invalid data for update", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse badValidationHandle(final MethodArgumentNotValidException e) {
        String errorMessage = e.getBindingResult()
                .getAllErrors()
                .get(0)
                .getDefaultMessage();

        log.warn("{}: {}", e.getClass().getSimpleName(), errorMessage);
        return new ErrorResponse("Validation error", errorMessage);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse badValidationHandle(final ConstraintViolationException e) {
        log.warn("{}: {}", e.getClass().getSimpleName(), e.getMessage());
        return new ErrorResponse("Validation error", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse exceptionHandle(final Exception e) {
        log.warn("{}: {}", e.getClass().getSimpleName(), e.getMessage());
        return new ErrorResponse("Server error", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse dbError(final SQLException e) {
        log.warn("{}: {}", e.getClass().getSimpleName(), e.getMessage());
        return new ErrorResponse("Internal error while working with db", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handle(final UserFeedFieldNotFoundException e) {
        log.warn(e.getMessage());
        return new ErrorResponse("Internal error ", e.getMessage());
    }
}
