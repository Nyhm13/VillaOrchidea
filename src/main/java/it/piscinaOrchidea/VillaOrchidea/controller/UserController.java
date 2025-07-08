package it.piscinaOrchidea.VillaOrchidea.controller;

import it.piscinaOrchidea.VillaOrchidea.dto.UserDto;
import it.piscinaOrchidea.VillaOrchidea.exceptions.EntitaGiaEsistente;
import it.piscinaOrchidea.VillaOrchidea.exceptions.NotFoundException;
import it.piscinaOrchidea.VillaOrchidea.exceptions.ValidationException;
import it.piscinaOrchidea.VillaOrchidea.model.User;
import it.piscinaOrchidea.VillaOrchidea.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping
    public User registerUser(@RequestBody @Validated UserDto userDto, BindingResult bindingResult) throws EntitaGiaEsistente, ValidationException {
        if (bindingResult.hasErrors()){
            throw  new ValidationException(bindingResult.getAllErrors().stream().map(objectError -> objectError.getDefaultMessage()).reduce("",(e, s)->e+s));
        }
        return userService.saveUser(userDto);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public List<User> getAllUsers(){
        return userService.getAllUsers();
    }
    @GetMapping("/me")
    @PreAuthorize("hasAnyAuthority('USER','ADMIN')")
    public User getMyProfile(Principal principal) throws NotFoundException {
        String username = principal.getName();
        return userService.getUserByUsername(username);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public User getUserById(@PathVariable Long id) throws NotFoundException {
        return userService.getUser(id);
    }

    @PutMapping("/me")
    @PreAuthorize("hasAnyAuthority('USER','ADMIN')")
    public User updateMyProfile(@RequestBody @Validated UserDto userDto, Principal principal,BindingResult bindingResult) throws NotFoundException, ValidationException {
        if (bindingResult.hasErrors()){
            throw  new ValidationException(bindingResult.getAllErrors().stream().map(objectError -> objectError.getDefaultMessage()).reduce("",(e, s)->e+s));
        }
        String username = principal.getName();
        User user = userService.getUserByUsername(username);
        return userService.updateUser(user.getId(), userDto);
    }

    @DeleteMapping("/me")
    @PreAuthorize("hasAuthority('USER')")
    public void deleteMyProfile(Principal principal) throws NotFoundException {
        String username = principal.getName();
        User user = userService.getUserByUsername(username);
        userService.deleteUser(user.getId());
    }


    //admin can delete any user
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public void deleteUserByAdmin(@PathVariable Long id) throws NotFoundException {
        userService.deleteUser(id);
    }


}
