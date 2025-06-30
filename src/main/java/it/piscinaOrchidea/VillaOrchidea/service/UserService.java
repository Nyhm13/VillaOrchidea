package it.piscinaOrchidea.VillaOrchidea.service;

import it.piscinaOrchidea.VillaOrchidea.dto.UserDto;
import it.piscinaOrchidea.VillaOrchidea.enumerations.Role;
import it.piscinaOrchidea.VillaOrchidea.exceptions.EntitaGiaEsistente;
import it.piscinaOrchidea.VillaOrchidea.exceptions.NotFoundException;
import it.piscinaOrchidea.VillaOrchidea.model.User;
import it.piscinaOrchidea.VillaOrchidea.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    public User saveUser(UserDto userDto) throws EntitaGiaEsistente {
        User user= new User();
        if (userRepository.existsByUsername(userDto.getUsername())){
            throw  new EntitaGiaEsistente("User con username " + userDto.getUsername() + " gia esistente");
        }
        if (userRepository.existsByEmail(userDto.getEmail())){
            throw  new EntitaGiaEsistente("User con email " + userDto.getEmail() + " gia esistente");
        }

        user.setNome(userDto.getNome());
        user.setCognome(userDto.getCognome());
        user.setEmail(userDto.getEmail());
        user.setRuolo(Role.USER);
        user.setTelefono(userDto.getTelefono());
        user.setUsername(userDto.getUsername());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        return  userRepository.save(user);
    }

    public List<User> getAllUsers(){
        return userRepository.findAll();
    }

    public User getUser(Long id) throws NotFoundException {
        return userRepository.findById(id).orElseThrow(() -> new NotFoundException("User con ID " + id + " non trovato"));
    }

    public User updateUser(Long id, UserDto userDto) throws NotFoundException {
        User userDaAggiornare = getUser(id);

        userDaAggiornare.setNome(userDto.getNome());
        userDaAggiornare.setCognome(userDto.getCognome());
        userDaAggiornare.setEmail(userDto.getEmail());
        userDaAggiornare.setTelefono(userDto.getTelefono());
        userDaAggiornare.setUsername(userDto.getUsername());

        if (!passwordEncoder.matches(userDto.getPassword(),userDaAggiornare.getPassword())){
            userDaAggiornare.setPassword(passwordEncoder.encode(userDto.getPassword()));
        }
        return userRepository.save(userDaAggiornare);
    }
    public void deleteUser(Long id) throws NotFoundException {
        User userDaCancellare = getUser(id);
        userRepository.delete(userDaCancellare);
    }
}
