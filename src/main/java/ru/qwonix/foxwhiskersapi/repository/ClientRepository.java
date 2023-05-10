package ru.qwonix.foxwhiskersapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.qwonix.foxwhiskersapi.entity.Client;

import java.util.Optional;

public interface ClientRepository extends JpaRepository<Client, Long> {



    Optional<Client> findByEmail(String email);

    boolean existsByEmail(String email);
}
