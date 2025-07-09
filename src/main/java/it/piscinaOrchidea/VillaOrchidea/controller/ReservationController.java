package it.piscinaOrchidea.VillaOrchidea.controller;

import it.piscinaOrchidea.VillaOrchidea.dto.ReservationDto;
import it.piscinaOrchidea.VillaOrchidea.enumerations.FasciaOraria;
import it.piscinaOrchidea.VillaOrchidea.exceptions.NotFoundException;
import it.piscinaOrchidea.VillaOrchidea.exceptions.ValidationException;
import it.piscinaOrchidea.VillaOrchidea.model.Reservation;
import it.piscinaOrchidea.VillaOrchidea.service.ReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/reservations")
public class ReservationController {

    @Autowired
    private ReservationService reservationService;


    @PostMapping("")
    @PreAuthorize("hasAnyAuthority('USER','ADMIN')")
    public Reservation createReservation(@RequestBody @Validated ReservationDto reservationDto, Principal principal, BindingResult bindingResult) throws NotFoundException, ValidationException {
        if (bindingResult.hasErrors()){
            throw  new ValidationException(bindingResult.getAllErrors().stream().map(objectError -> objectError.getDefaultMessage()).reduce("",(e, s)->e+s));
        }
        String username = principal.getName();

        return  reservationService.saveReservation(reservationDto,username);
    }

    @GetMapping("")
    @PreAuthorize("hasAuthority('ADMIN')")
    public List<Reservation> getReservations(@RequestParam(value = "date", required = false) LocalDate date) {
        if (date != null) {
            return reservationService.getReservationsByDate(date);
        } else {
            return reservationService.getAllReservations();
        }
    }
    @GetMapping("/me")
    @PreAuthorize("hasAnyAuthority('USER','ADMIN')")
    public List<Reservation> getMyReservations(Principal principal) throws NotFoundException {
        String username = principal.getName();
        return reservationService.getReservationsByUser(username);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public Reservation getReservation(@PathVariable Long id) throws NotFoundException {
        return reservationService.getReservation(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('USER','ADMIN')")
    public Reservation updateReservation(@PathVariable Long id, @RequestBody @Validated ReservationDto reservationDto, Principal principal,BindingResult bindingResult) throws NotFoundException, ValidationException {
        if (bindingResult.hasErrors()){
            throw  new ValidationException(bindingResult.getAllErrors().stream().map(objectError -> objectError.getDefaultMessage()).reduce("",(e, s)->e+s));
        }
        String username = principal.getName();
        return reservationService.updateReservation(id, reservationDto, username);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('USER','ADMIN')")
    public void deleteReservation(@PathVariable Long id,Principal principal) throws NotFoundException {
        reservationService.deleteReservation(id, principal.getName());
    }
    //-------testing-----

    @GetMapping("/availability")
    @PreAuthorize("hasAnyAuthority('USER','ADMIN')")
    public Map<FasciaOraria, Integer> getAvailability(@RequestParam("data") LocalDate data) {
        return reservationService.getAvailability(data);
    }

}
