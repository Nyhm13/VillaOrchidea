package it.piscinaOrchidea.VillaOrchidea.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserDto {
    @Email(message = "il campo email  deve contenere un indirizzo email valido ")
    @NotEmpty(message = "il campo email non puo essere nullo ")
    private  String email;
    @NotEmpty(message = "il campo username non puo essere nullo ")
    private  String username;
    @NotEmpty(message = "il campo password non puo essere nullo ")
    private String password;
    @NotEmpty(message = "il campo nome non puo essere nullo ")
    private String nome;
    @NotEmpty(message = "il campo cognome non puo essere nullo ")
    private String cognome;
    @NotEmpty(message = "il campo telefono  non puo essere nullo ")
    private String telefono;
}
