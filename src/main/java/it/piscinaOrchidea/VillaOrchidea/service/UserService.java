package it.piscinaOrchidea.VillaOrchidea.service;

import it.piscinaOrchidea.VillaOrchidea.dto.UserDto;
import it.piscinaOrchidea.VillaOrchidea.enumerations.Role;
import it.piscinaOrchidea.VillaOrchidea.exceptions.BadRequestException;
import it.piscinaOrchidea.VillaOrchidea.exceptions.EntitaGiaEsistente;
import it.piscinaOrchidea.VillaOrchidea.exceptions.NotFoundException;
import it.piscinaOrchidea.VillaOrchidea.model.User;
import it.piscinaOrchidea.VillaOrchidea.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JavaMailSenderImpl javaMailSender;


    public User saveUser(UserDto userDto) throws EntitaGiaEsistente {
        User user = new User();
        if (userRepository.existsByUsername(userDto.getUsername())) {
            throw new EntitaGiaEsistente("User con username " + userDto.getUsername() + " gia esistente");
        }
        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new EntitaGiaEsistente("User con email " + userDto.getEmail() + " gia esistente");
        }
        if (!userDto.getTelefono().matches("^\\+\\d{1,4}\\s?(\\d\\s?){6,15}$")
                || userDto.getTelefono().length() > 20) {
            throw new BadRequestException("Il numero di telefono non è valido, deve avere un prefisso +39 seguito dal numero di telefono ");
        }

        user.setNome(userDto.getNome());
        user.setCognome(userDto.getCognome());
        user.setEmail(userDto.getEmail());
        user.setRuolo(Role.USER);
        user.setTelefono(userDto.getTelefono());
        user.setUsername(userDto.getUsername());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        User savedUser = userRepository.save(user);
        sendMail(
                savedUser.getEmail(),
                savedUser.getUsername(),
                savedUser.getNome(),
                savedUser.getCognome(),
                savedUser.getId(),
                userDto.getPassword()
        );
        return savedUser;

    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUser(Long id) throws NotFoundException {
        return userRepository.findById(id).orElseThrow(() -> new NotFoundException("User con ID " + id + " non trovato"));
    }

    public User getUserByUsername(String username) throws NotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("Utente con username " + username + " non trovato"));
    }

    public User updateUser(Long id, UserDto userDto) throws NotFoundException, EntitaGiaEsistente  {
        User userDaAggiornare = getUser(id);

        if (userRepository.existsByUsername(userDto.getUsername())
                && !userDto.getUsername().equals(userDaAggiornare.getUsername())) {
            throw new EntitaGiaEsistente("Username " + userDto.getUsername() + " già esistente");
        }

        if (userRepository.existsByEmail(userDto.getEmail())
                && !userDto.getEmail().equals(userDaAggiornare.getEmail())) {
            throw new EntitaGiaEsistente("Email " + userDto.getEmail() + " già esistente");
        }
        if (!userDto.getTelefono().matches("^\\+\\d{1,4}\\s?(\\d\\s?){6,15}$")
                || userDto.getTelefono().length() > 20) {
            throw new BadRequestException("Il numero di telefono non è valido, deve avere un prefisso es :+39 seguito dal numero di telefono ");
        }

        userDaAggiornare.setNome(userDto.getNome());
        userDaAggiornare.setCognome(userDto.getCognome());
        userDaAggiornare.setEmail(userDto.getEmail());
        userDaAggiornare.setTelefono(userDto.getTelefono());
        userDaAggiornare.setUsername(userDto.getUsername());

        if (!passwordEncoder.matches(userDto.getPassword(), userDaAggiornare.getPassword())) {
            userDaAggiornare.setPassword(passwordEncoder.encode(userDto.getPassword()));
        }
        
        User updatedUser = userRepository.save(userDaAggiornare);

        sendMailAggiornamento(updatedUser);

        return updatedUser;
    }

    public void deleteUser(Long id) throws NotFoundException {
        User userDaCancellare = getUser(id);
        userRepository.delete(userDaCancellare);
    }

    private void sendMail(String email, String username, String nome, String cognome, Long id, String password) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Benvenuto nella Villa Orchidea!");

        message.setText(
                "Ciao " + nome + " " + cognome + ",\n\n" +
                        "La tua registrazione a Villa Orchidea è avvenuta con successo.\n\n" +
                        "👉 Dati del tuo account:\n" +
                        "- ID utente: " + id + "\n" +
                        "- Username: " + username + "\n" +
                        "- Password: " + password + "\n\n" +
                        "Ora puoi accedere alla tua area riservata per gestire le prenotazioni in piscina.\n\n" +
                        "Ricorda di conservare le tue credenziali in un luogo sicuro.\n\n" +
                        "Buon relax e grazie per averci scelto!\n\n" +
                        "Lo staff di Villa Orchidea"
        );

        javaMailSender.send(message);

    }

    private void sendMailAggiornamento(User user) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject("Villa Orchidea - Aggiornamento Profilo");

        message.setText(
                "Ciao " + user.getNome() + " " + user.getCognome() + ",\n\n" +
                        "Il tuo profilo è stato aggiornato con successo.\n\n" +
                        "👉 Dati aggiornati:\n" +
                        "- Username: " + user.getUsername() + "\n" +
                        "- Email: " + user.getEmail() + "\n" +
                        "- Telefono: " + user.getTelefono() + "\n\n" +
                        "Se non hai richiesto queste modifiche contatta subito il nostro staff.\n\n" +
                        "Grazie per aver scelto Villa Orchidea!\n\n" +
                        "Lo staff di Villa Orchidea"
        );

        javaMailSender.send(message);
    }
}
