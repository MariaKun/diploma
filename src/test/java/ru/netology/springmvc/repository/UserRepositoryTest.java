package ru.netology.springmvc.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.testcontainers.shaded.org.apache.commons.lang3.RandomStringUtils;
import ru.netology.springmvc.entity.User;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static ru.netology.springmvc.TestData.randomUser;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void findByUsername() {
        User user = randomUser();
        userRepository.save(user);

        User user2 = randomUser();
        userRepository.save(user2);

        Optional<User> byUsername = userRepository.findByUsername(user.getUsername());
        assertEquals(user.getUsername(), byUsername.orElseThrow().getUsername());
    }

    @Test
    void findByUsername_userNotExists() {
        Optional<User> byUsername = userRepository.findByUsername(RandomStringUtils.randomAlphabetic(5));
        assertEquals(Optional.empty(), byUsername);
    }

    @Test
    void existsByUsername() {
        User user = randomUser();
        userRepository.save(user);
        boolean exist = userRepository.existsByUsername(user.getUsername());
        assertTrue(exist);
    }

    @Test
    void existsByUsername_notExists() {
        boolean exist = userRepository.existsByUsername(RandomStringUtils.randomAlphabetic(5));
        assertFalse(exist);
    }
}