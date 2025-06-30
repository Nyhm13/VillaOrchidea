package it.piscinaOrchidea.VillaOrchidea.service;

import it.piscinaOrchidea.VillaOrchidea.dto.ReservationDto;
import it.piscinaOrchidea.VillaOrchidea.enumerations.FasciaOraria;
import it.piscinaOrchidea.VillaOrchidea.exceptions.BadRequestException;
import it.piscinaOrchidea.VillaOrchidea.exceptions.NotFoundException;
import it.piscinaOrchidea.VillaOrchidea.model.Reservation;
import it.piscinaOrchidea.VillaOrchidea.model.User;
import it.piscinaOrchidea.VillaOrchidea.repository.ReservationRepository;
import it.piscinaOrchidea.VillaOrchidea.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

@Service
public class ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JavaMailSenderImpl javaMailSender;


    private static final int POSTI_TOTALI =50;

    public Reservation saveReservation(ReservationDto reservationDto,String username) throws NotFoundException, BadRequestException {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new NotFoundException("Utente non trovato"));

        LocalDate data= reservationDto.getDataPrenotazione();

        if (data.getDayOfWeek() == DayOfWeek.MONDAY){
            throw  new BadRequestException("La piscina è chiusa il lunedì, non puoi prenotare in questo giorno");
        }

        LocalDate inizioStagione=LocalDate.of(data.getYear(), 6,20);
        LocalDate fineStagione=LocalDate.of(data.getYear(), 9,15);
        if (data.isBefore(inizioStagione) || data.isAfter(fineStagione)){
            throw new BadRequestException("La piscina è aperta solo dal 20 Giugno al 15 Settembre, non puoi prenotare in questo periodo");
        }

        if (reservationRepository.existsByUserAndDataPrenotazione(user,data)){
            throw  new BadRequestException("Hai già una prenotazione per questa data");
        }

        int occupati = calcolaPostiOccupati(data,reservationDto.getFasciaOraria());

        int disponibili = POSTI_TOTALI - occupati;

        if (reservationDto.getNumeroPosti()>disponibili){
            throw  new BadRequestException(  "Posti insufficienti: hai richiesto " + reservationDto.getNumeroPosti() +
                    ", ma sono disponibili solo " + disponibili + " posti per questa fascia oraria.");
        }

        Reservation reservation= new Reservation();
        reservation.setUser(user);
        reservation.setNumeroPosti(reservationDto.getNumeroPosti());
        reservation.setFasciaOraria(reservationDto.getFasciaOraria());
        reservation.setDataPrenotazione(data);
        reservation.setNote(reservationDto.getNote());

        Reservation savedReservation = reservationRepository.save(reservation);
        sendMail(user, savedReservation, "creata");
        return savedReservation;

    }

    public Reservation getReservation(Long id) throws NotFoundException {
        return reservationRepository.findById(id).orElseThrow(() -> new NotFoundException(" Prenotazione non trovata con id " + id));
    }

    public List<Reservation> getAllReservations(){
        return  reservationRepository.findAll();
    }

    public List<Reservation> getReservationsByDate(LocalDate dataPrenotazione){
        return reservationRepository.findAllByDataPrenotazione(dataPrenotazione);
    }

    public List <Reservation> getReservationsByUser(String username) throws NotFoundException {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new NotFoundException("Utente con username " + username + " non trovato"));

        return reservationRepository.findAllByUser(user);

    }

    public void deleteReservation(Long id) throws NotFoundException {
        Reservation reservation=reservationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Prenotazione non trovata con id " + id));

        User user = reservation.getUser();
        sendMail(user, reservation, "cancellata");

        reservationRepository.delete(reservation);
    }


    public Reservation updateReservation(Long id, ReservationDto reservationDto,String username) throws NotFoundException {
        User user= userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("Utente con questo " + username+
                        " non trovato"));
        Reservation existing= reservationRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Prenotazione con id " + id + " non trovata"));
        LocalDate data =reservationDto.getDataPrenotazione();

        if(data.getDayOfWeek()== DayOfWeek.MONDAY){
            throw  new BadRequestException("La piscina è chiusa il lunedì, non puoi prenotare in questo giorno");
        }
        LocalDate inizioStagione=LocalDate.of(data.getYear(), 6,20);
        LocalDate fineStagione=LocalDate.of(data.getYear(), 9,15);
        if (data.isBefore(inizioStagione) || data.isAfter(fineStagione)){
            throw new BadRequestException("La piscina è aperta solo dal 20 Giugno al 15 Settembre, non puoi prenotare in questo periodo");
        }
        if (!existing.getDataPrenotazione().equals(data)){
            if (reservationRepository.existsByUserAndDataPrenotazione(user,data)){
                throw new BadRequestException(" Hai già una prenotazione per questa data");
            }
        }

        int occupati= calcolaPostiOccupati(data,reservationDto.getFasciaOraria());
        occupati-= existing.getNumeroPosti();
        int disponibili=POSTI_TOTALI-occupati;

        if (reservationDto.getNumeroPosti()>disponibili){
            throw  new BadRequestException(  "Posti insufficienti: hai richiesto " + reservationDto.getNumeroPosti() +
                    ", ma sono disponibili solo " + disponibili + " posti per questa fascia oraria.");
        }
        existing.setNumeroPosti(reservationDto.getNumeroPosti());
        existing.setFasciaOraria(reservationDto.getFasciaOraria());
        existing.setDataPrenotazione(data);
        existing.setNote(reservationDto.getNote());

        Reservation updated = reservationRepository.save(existing);
        sendMail(user, updated, "modificata");
        return updated;

    }



    private int calcolaPostiOccupati(LocalDate data, FasciaOraria fasciaOraria) {
        switch (fasciaOraria) {
            case FULL_DAY:
                return reservationRepository.sumPostiPerFascia(data, FasciaOraria.FULL_DAY).orElse(0)
                        + reservationRepository.sumPostiPerFascia(data, FasciaOraria.MORNING).orElse(0)
                        + reservationRepository.sumPostiPerFascia(data, FasciaOraria.AFTERNOON).orElse(0);
            case MORNING:
                return reservationRepository.sumPostiPerFascia(data, FasciaOraria.FULL_DAY).orElse(0)
                        + reservationRepository.sumPostiPerFascia(data, FasciaOraria.MORNING).orElse(0);
            case AFTERNOON:
                return reservationRepository.sumPostiPerFascia(data, FasciaOraria.FULL_DAY).orElse(0)
                        + reservationRepository.sumPostiPerFascia(data, FasciaOraria.AFTERNOON).orElse(0);
            default:
                throw new BadRequestException("Fascia oraria non valida");
        }
    }

    private void sendMail(User user, Reservation reservation, String azione) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject("Villa Orchidea - Notifica " + azione + " prenotazione piscina");

        String testo = "Ciao " + user.getNome() + " " + user.getCognome() + ",\n\n"
                + "La tua prenotazione è stata " + azione + " con successo.\n\n"
                + "📅 Data: " + reservation.getDataPrenotazione() + "\n"
                + "🕘 Fascia oraria: " + reservation.getFasciaOraria() + "\n"
                + "👥 Posti prenotati: " + reservation.getNumeroPosti() + "\n"
                + (reservation.getNote() != null ? "📝 Note: " + reservation.getNote() + "\n\n" : "\n")
                + "Grazie per aver scelto Villa Orchidea!\n"
                + "Buon relax!";

        message.setText(testo);

        javaMailSender.send(message);
    }
}
