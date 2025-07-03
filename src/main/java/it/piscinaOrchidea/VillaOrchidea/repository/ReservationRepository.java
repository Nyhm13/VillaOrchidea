package it.piscinaOrchidea.VillaOrchidea.repository;

import it.piscinaOrchidea.VillaOrchidea.enumerations.FasciaOraria;
import it.piscinaOrchidea.VillaOrchidea.model.Reservation;
import it.piscinaOrchidea.VillaOrchidea.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation,Long> {

    boolean existsByUserAndDataPrenotazione(User user, LocalDate dataPrenotazione);

    // This method sums the number of seats reserved in total by fascia oraria for a specific date ADMIN

//    @Query("""
//    SELECT COALESCE(SUM(r.numeroPosti), 0)
//    FROM Reservation r
//    WHERE r.dataPrenotazione = :date
//    AND (r.fasciaOraria = :fascia OR r.fasciaOraria = :full)
//""")
//    Optional<Integer> sumPostiPerFascia(
//            @Param("date") LocalDate date,
//            @Param("fascia") FasciaOraria fascia,
//            @Param("full") FasciaOraria full
//    );

    @Query("""
    SELECT COALESCE(SUM(r.numeroPosti), 0)
    FROM Reservation r
    WHERE r.dataPrenotazione = :date
    AND r.fasciaOraria IN (:fasce)
""")
    Optional<Integer> sumPostiPerFascia(
            @Param("date") LocalDate date,
            @Param("fasce") List<FasciaOraria> fasce
    );
    // This method sums the number of seats reserved in the morning for a specific date ADMIN

    @Query("""
        SELECT COALESCE(SUM(r.numeroPosti), 0)
        FROM Reservation r
        WHERE r.dataPrenotazione = :date
        AND (r.fasciaOraria = 'FULLDAY' OR r.fasciaOraria = 'MORNING')
    """)
    Optional<Integer> sumPostiMattina(@Param("date") LocalDate date);

    // This method sums the number of seats reserved in the afternoon for a specific date ADMIN
    @Query("""
        SELECT COALESCE(SUM(r.numeroPosti), 0)
        FROM Reservation r
        WHERE r.dataPrenotazione = :date
        AND (r.fasciaOraria = 'FULLDAY' OR r.fasciaOraria = 'AFTERNOON')
    """)
    Optional<Integer> sumPostiPomeriggio(@Param("date") LocalDate date);

    // Find all reservations made on a specific date for ADMIN
    List<Reservation> findAllByDataPrenotazione(LocalDate dataPrenotazione);

    // Find all reservations made by a specific user for USER
    List<Reservation> findAllByUser(User user);

    @Query("SELECT SUM(r.numeroPosti) FROM Reservation r WHERE r.dataPrenotazione = :data AND r.fasciaOraria IN :fasce AND r.id <> :excludeId")
    Optional<Integer> sumPostiPerFasciaEscludiId(@Param("data") LocalDate data,
                                                 @Param("fasce") List<FasciaOraria> fasce,
                                                 @Param("excludeId") Long excludeId);

}

