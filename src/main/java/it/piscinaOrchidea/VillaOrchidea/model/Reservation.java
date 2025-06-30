package it.piscinaOrchidea.VillaOrchidea.model;

import it.piscinaOrchidea.VillaOrchidea.enumerations.FasciaOraria;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn (name = "user_id")
    private User user;
    @Enumerated(EnumType.STRING)
    private FasciaOraria fasciaOraria;
    private Integer numeroPosti;
    private LocalDate dataPrenotazione;
    private String note;
}
