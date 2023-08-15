package ru.qwonix.foxwhiskersapi.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.qwonix.foxwhiskersapi.dto.UpdateUserDTO;
import ru.qwonix.foxwhiskersapi.entity.Role;
import ru.qwonix.foxwhiskersapi.entity.User;
import ru.qwonix.foxwhiskersapi.service.UserService;

@Slf4j
@RestController
@RequestMapping("/api/v1/user")
public class UserRestController {

    private final UserService userService;

    public UserRestController(UserService userService) {
        this.userService = userService;
    }

    @PatchMapping
    public ResponseEntity<User> patch(@AuthenticationPrincipal org.springframework.security.core.userdetails.User authenticationUser, @RequestBody UpdateUserDTO updateUserDTO) {
        log.info("user {} send request to update account info {}", authenticationUser.getUsername(), updateUserDTO);

        var optionalUser = userService.findByPhoneNumber(authenticationUser.getUsername());
        if (optionalUser.isPresent()) {
            var user = optionalUser.get();

            if (updateUserDTO.phoneNumber() != null) {
                // TODO: 12.08.2023 to update phone number need confirmation
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }

            if (updateUserDTO.email() != null) {
                // TODO: 12.08.2023 to update email need confirmation
                user.setEmail(updateUserDTO.email());
                user.setRole(Role.CLIENT);
            }

            if (updateUserDTO.firstName() != null) {
                user.setFirstName(updateUserDTO.firstName());
            }
            if (updateUserDTO.lastName() != null) {
                user.setLastName(updateUserDTO.lastName());
            }
            if (updateUserDTO.middleName() != null) {
                user.setMiddleName(updateUserDTO.middleName());
            }

            return ResponseEntity.ok(userService.update(user));
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
