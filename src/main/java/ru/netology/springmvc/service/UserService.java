package ru.netology.springmvc.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import ru.netology.springmvc.entity.User;
import ru.netology.springmvc.exception.InvalidCredentials;
import ru.netology.springmvc.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository repository;

    public User create(User user) {
        if (repository.existsByUsername(user.getUsername())) {
            throw new InvalidCredentials("User already exists");
        }

        if (repository.existsByEmail(user.getEmail())) {
            throw new InvalidCredentials("User already exists");
        }
        return repository.save(user);
    }

    public User getByUsername(String username) {
        return repository.findByUsername(username)
                .orElseThrow(() -> new InvalidCredentials("User not found"));
    }

    public UserDetailsService userDetailsService() {
        return this::getByUsername;
    }

    public User getCurrentUser() {
        return getByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
    }
}