package ru.netology.springmvc.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.testcontainers.shaded.org.apache.commons.lang3.RandomStringUtils;
import ru.netology.springmvc.entity.User;
import ru.netology.springmvc.exception.InvalidCredentials;
import ru.netology.springmvc.exception.UserNotFound;
import ru.netology.springmvc.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static ru.netology.springmvc.TestData.randomUser;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    UserService userService;

    @Mock
    UserRepository userRepository;

    private final User testUser = randomUser();

    @Test
    void create() {
        when(userRepository.existsByUsername(testUser.getUsername())).thenReturn(false);
        when(userRepository.save(testUser)).thenReturn(testUser);

        User user = userService.create(testUser);
        assertEquals(testUser, user);
        verify(userRepository, times(1)).existsByUsername(testUser.getUsername());
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    void create_userNameExists() {
        when(userRepository.existsByUsername(testUser.getUsername())).thenReturn(true);

        assertThrows(InvalidCredentials.class, () -> userService.create(testUser));
        verify(userRepository, times(1)).existsByUsername(testUser.getUsername());
    }

    @Test
    void getByUsername() {
        when(userRepository.findByUsername(testUser.getUsername())).thenReturn(Optional.of(testUser));

        User userServiceByUsername = userService.getByUsername(testUser.getUsername());
        assertEquals(testUser, userServiceByUsername);
        verify(userRepository, times(1)).findByUsername(testUser.getUsername());
    }

    @Test
    void getByUsername_notFound() {
        String username = RandomStringUtils.randomAlphabetic(5);
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        assertThrows(UserNotFound.class, () -> userService.getByUsername(username));
        verify(userRepository, times(1)).findByUsername(username);
    }

    @Test
    void getCurrentUser() {
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = Mockito.mock(Authentication.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
        when(SecurityContextHolder.getContext().getAuthentication().getName()).thenReturn(testUser.getUsername());
        when(userRepository.findByUsername(testUser.getUsername())).thenReturn(Optional.of(testUser));

        User currentUser = userService.getCurrentUser();
        assertEquals(testUser, currentUser);
        verify(userRepository, times(1)).findByUsername(testUser.getUsername());
    }

    @Test
    void loadUserByUsername() {
        when(userRepository.findByUsername(testUser.getUsername())).thenReturn(Optional.of(testUser));

        UserDetails userDetails = userService.loadUserByUsername(testUser.getUsername());
        assertEquals(testUser.getUsername(), userDetails.getUsername());
        verify(userRepository, times(1)).findByUsername(testUser.getUsername());
    }

    @Test
    void loadUserByUsername_notFound() {
        when(userRepository.findByUsername(testUser.getUsername())).thenReturn(Optional.empty());

        assertThrows(UserNotFound.class, () -> userService.loadUserByUsername(testUser.getUsername()));
        verify(userRepository, times(1)).findByUsername(testUser.getUsername());
    }
}


