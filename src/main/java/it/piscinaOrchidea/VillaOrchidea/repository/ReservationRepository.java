package it.piscinaOrchidea.VillaOrchidea.repository;

import it.piscinaOrchidea.VillaOrchidea.enumerations.FasciaOraria;
import it.piscinaOrchidea.VillaOrchidea.model.Reservation;
import it.piscinaOrchidea.VillaOrchidea.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation,Long> {

    boolean existsByUserAndDataPrenotazione(User user, LocalDate dataPrenotazione);

    // This method sums the number of seats reserved in total by fascia oraria for a specific date ADMIN

    @Query("""
        SELECT COALESCE(SUM(r.numeroPosti), 0)
        FROM Reservation r
        WHERE r.dataPrenotazione = :date
        AND (r.fasciaOraria = :fascia OR r.fasciaOraria = 'FULL_DAY')
    """)
    Integer sumPostiPerFascia(
            @Param("date") LocalDate date,
            @Param("fascia") FasciaOraria fascia
    );
    // This method sums the number of seats reserved in the morning for a specific date ADMIN

    @Query("""
        SELECT COALESCE(SUM(r.numeroPosti), 0)
        FROM Reservation r
        WHERE r.dataPrenotazione = :date
        AND (r.fasciaOraria = 'FULL_DAY' OR r.fasciaOraria = 'MORNING')
    """)
    Integer sumPostiMattina(@Param("date") LocalDate date);

    // This method sums the number of seats reserved in the afternoon for a specific date ADMIN
    @Query("""
        SELECT COALESCE(SUM(r.numeroPosti), 0)
        FROM Reservation r
        WHERE r.dataPrenotazione = :date
        AND (r.fasciaOraria = 'FULL_DAY' OR r.fasciaOraria = 'AFTERNOON')
    """)
    Integer sumPostiPomeriggio(@Param("date") LocalDate date);

    // Find all reservations made on a specific date for ADMIN
    List<Reservation> findAllByDataPrenotazione(LocalDate dataPrenotazione);

    // Find all reservations made by a specific user for USER
    List<Reservation> findAllByUser(User user);

}
