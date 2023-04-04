package ru.qwonix.foxwhiskersapi.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.qwonix.foxwhiskersapi.entity.User;
import ru.qwonix.foxwhiskersapi.repository.UserRepository;
import ru.qwonix.foxwhiskersapi.service.UserService;

import java.util.List;
import java.util.Optional;


@Slf4j
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public List<User> getAll() {
        List<User> result = userRepository.findAll();
        log.info("IN getAll - {} users found", result.size());
        return result;
    }

    @Override
    public Optional<User> findByEmail(String email) {
        Optional<User> result = userRepository.findByEmail(email);
        log.info("IN findByUsername - user: {} found by username: {}", result, email);
        return result;
    }

    @Override
    public User findById(Long id) {
        return userRepository.findById(id).orElseGet(() -> {
            log.warn("IN findById - no user found by id: {}", id);
            return null;
        });
    }

    @Override
    public void delete(Long id) {
        userRepository.deleteById(id);
        log.info("IN delete - user with id: {} successfully deleted", id);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
}
