package ru.netology.springmvc.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.netology.springmvc.entity.Role;
import ru.netology.springmvc.entity.User;
import ru.netology.springmvc.repository.FileRepository;
import ru.netology.springmvc.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    UserService userService;
    @Mock
    UserRepository userRepository;

    @Test
    void create() {
    }

    @Test
    void getByUsername() {
        User user = new User();
        user.setEmail("f@g");
        user.setUsername("test");
        user.setPassword("123");
        user.setRole(Role.ROLE_USER);
        when(userRepository.findByUsername("b")).thenReturn(Optional.of(user));
        User b = userService.getByUsername("b");
        assertEquals(user, b);
        verify(userRepository, times(1)).findByUsername("b");
    }

    @Test
    void userDetailsService() {
    }

    @Test
    void getCurrentUser() {
    }
}