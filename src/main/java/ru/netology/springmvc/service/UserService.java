package ru.netology.springmvc.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import ru.netology.springmvc.entity.User;
import ru.netology.springmvc.exception.InvalidCredentials;
import ru.netology.springmvc.exception.UserNotFound;
import ru.netology.springmvc.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository repository;

    public User create(User user) {
        if (repository.existsByEmail(user.getEmail())) {
            throw new InvalidCredentials("User already exists");
        }
        if (repository.existsByUsername(user.getUsername())) {
            throw new InvalidCredentials("User already exists");
        }
        return repository.save(user);
    }

    public User getByUsername(String username) {
        return repository.findByUsername(username)
                .orElseThrow(() -> new UserNotFound("User not found"));
    }

    @Override
    public UserDetails loadUserByUsername(String login) {
        return repository.findByUsername(login)
                .orElseThrow(() -> new UserNotFound("User not found"));
    }

    public User getCurrentUser() {
        return getByUsername(SecurityContextHolder.getContext().getAuthentication().getName());
    }
}