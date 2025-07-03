package it.piscinaOrchidea.VillaOrchidea.dto;

import it.piscinaOrchidea.VillaOrchidea.enumerations.FasciaOraria;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ReservationDto {
    @Min(value = 1, message = "Il numero di posti deve essere almeno 1")
    @NotNull(message = "Il campo 'numeroPosti' non può essere nullo")
    private Integer numeroPosti;
    @NotNull(message = "Il campo 'fasciaOraria' non può essere nullo")
    private FasciaOraria fasciaOraria;
    @NotNull(message = "La data e obbligatoria ")
    private LocalDate dataPrenotazione;
    private String note;
}
