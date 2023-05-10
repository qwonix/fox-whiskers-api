package ru.qwonix.foxwhiskersapi.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.qwonix.foxwhiskersapi.entity.Client;
import ru.qwonix.foxwhiskersapi.repository.ClientRepository;
import ru.qwonix.foxwhiskersapi.service.ClientService;

import java.util.List;
import java.util.Optional;


@Slf4j
@Service
public class ClientServiceImpl implements ClientService {
    private final ClientRepository clientRepository;

    @Autowired
    public ClientServiceImpl(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Override
    public Client save(Client client) {
        return clientRepository.save(client);
    }

    @Override
    public List<Client> getAll() {
        List<Client> result = clientRepository.findAll();
        log.info("IN getAll - {} users found", result.size());
        return result;
    }

    @Override
    public Optional<Client> findByPhoneNumber(String phoneNumber) {
        Optional<Client> result = clientRepository.findByEmail(phoneNumber);
        log.info("IN findByUsername - client: {} found by username: {}", result, phoneNumber);
        return result;
    }

    @Override
    public Client findById(Long id) {
        return clientRepository.findById(id).orElseGet(() -> {
            log.warn("IN findById - no client found by id: {}", id);
            return null;
        });
    }

    @Override
    public void delete(Long id) {
        clientRepository.deleteById(id);
        log.info("IN delete - client with id: {} successfully deleted", id);
    }

    @Override
    public boolean exists(String email) {
        return clientRepository.existsByEmail(email);
    }
}
