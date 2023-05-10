package ru.qwonix.foxwhiskersapi.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.qwonix.foxwhiskersapi.dto.UpdateClientDTO;
import ru.qwonix.foxwhiskersapi.entity.Client;
import ru.qwonix.foxwhiskersapi.exception.UpdateException;
import ru.qwonix.foxwhiskersapi.service.ClientService;

import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/v1/client")
public class ClientRestController {

    private final ClientService clientService;

    public ClientRestController(ClientService clientService) {
        this.clientService = clientService;
    }

    //    @PreAuthorize("updateClientDTO.phoneNumber == authentication.principal")
    @PutMapping("/update")
    public ResponseEntity<Client> update(@RequestBody UpdateClientDTO updateClientDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info("{} - {}", authentication, authentication.getPrincipal());
        Optional<Client> byPhoneNumber = clientService.findByPhoneNumber(updateClientDTO.getPhoneNumber());
        if (byPhoneNumber.isPresent()) {
            Client client = byPhoneNumber.get();
            String firstName = updateClientDTO.getFirstName();
            String lastName = updateClientDTO.getLastName();
            String email = updateClientDTO.getEmail();
            if (firstName != null) {
                client.setFirstName(firstName);
            }
            if (lastName != null) {
                client.setLastName(lastName);
            }
            if (email != null) {
                client.setEmail(email);
            }

            return ResponseEntity.ok(clientService.save(client));
        } else {
            throw new UpdateException(HttpStatus.NOT_FOUND, "Клиента с таким номером нет");
        }
    }

}
