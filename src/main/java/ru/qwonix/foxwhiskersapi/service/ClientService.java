package ru.qwonix.foxwhiskersapi.service;

import ru.qwonix.foxwhiskersapi.entity.Client;

import java.util.List;
import java.util.Optional;

public interface ClientService {

    Client save(Client client);

    List<Client> getAll();

    Optional<Client> findByPhoneNumber(String phoneNumber);

    Client findById(Long id);

    void delete(Long id);

    boolean exists(String email);
}
