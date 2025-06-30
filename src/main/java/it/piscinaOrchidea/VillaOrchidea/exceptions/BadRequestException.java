package it.piscinaOrchidea.VillaOrchidea.exceptions;

public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}
