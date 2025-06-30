package it.piscinaOrchidea.VillaOrchidea.exceptions;

import it.piscinaOrchidea.VillaOrchidea.model.ApiError;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError notFoundException (NotFoundException e){
        ApiError error= new ApiError();
        error.setMessage(e.getMessage());
        error.setDataErrore(LocalDateTime.now());
        return  error;
    }

    @ExceptionHandler(ConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError notFoundException (ConflictException e){
        ApiError error= new ApiError();
        error.setMessage(e.getMessage());
        error.setDataErrore(LocalDateTime.now());
        return  error;
    }

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError notFoundException (ValidationException e){
        ApiError error= new ApiError();
        error.setMessage(e.getMessage());
        error.setDataErrore(LocalDateTime.now());
        return  error;
    }
    @ExceptionHandler(EntitaGiaEsistente.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError notFoundException (EntitaGiaEsistente e){
        ApiError error= new ApiError();
        error.setMessage(e.getMessage());
        error.setDataErrore(LocalDateTime.now());
        return  error;
    }
    @ExceptionHandler(PostiEsauritiException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError notFoundException (PostiEsauritiException e){
        ApiError error= new ApiError();
        error.setMessage(e.getMessage());
        error.setDataErrore(LocalDateTime.now());
        return  error;
    }
    @ExceptionHandler(InvalidOperationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError notFoundException (InvalidOperationException e){
        ApiError error= new ApiError();
        error.setMessage(e.getMessage());
        error.setDataErrore(LocalDateTime.now());
        return  error;
    }
}
