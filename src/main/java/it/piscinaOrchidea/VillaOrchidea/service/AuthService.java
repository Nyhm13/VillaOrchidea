package it.piscinaOrchidea.VillaOrchidea.service;


import it.piscinaOrchidea.VillaOrchidea.dto.LoginDto;
import it.piscinaOrchidea.VillaOrchidea.exceptions.NotFoundException;
import it.piscinaOrchidea.VillaOrchidea.model.User;
import it.piscinaOrchidea.VillaOrchidea.repository.UserRepository;
import it.piscinaOrchidea.VillaOrchidea.security.JwtTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtTool jwtTool;

    @Autowired
    private PasswordEncoder passwordEncoder;


    public String login (LoginDto loginDto) throws NotFoundException {
        User user =userRepository.findByUsernameAndEmail(loginDto.getUsername(),loginDto.getEmail()).
                orElseThrow(() -> new NotFoundException("Utente con questo username/email non trovato"));

        if (passwordEncoder.matches(loginDto.getPassword(),user.getPassword())){
            return jwtTool.createToken(user);
        } else {
            throw  new NotFoundException("Utente con questo username/password non trovato");
        }
    }
}
