package ru.qwonix.foxwhiskersapi.repository;


import ru.qwonix.foxwhiskersapi.entity.Client;

import java.util.Optional;
import java.util.UUID;

public interface ClientRepository extends CrudRepository<Client, UUID> {
    Optional<Client> findByPhoneNumber(String phoneNumber);

    boolean existsByPhoneNumber(String phoneNumber);

}
