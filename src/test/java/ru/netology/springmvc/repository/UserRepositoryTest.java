package ru.netology.springmvc.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.netology.springmvc.entity.Role;
import ru.netology.springmvc.entity.User;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void findByUsername() {
        User user = new User();
        user.setEmail("f@g");
        user.setUsername("test");
        user.setPassword("123");
        user.setRole(Role.ROLE_USER);
        userRepository.save(user);
        Optional<User> byUsername = userRepository.findByUsername(user.getUsername());
        assertEquals(user.getUsername(), byUsername.orElseThrow().getUsername());
    }

    @Test
    void existsByUsername() {
    }

    @Test
    void existsByEmail() {
    }
}