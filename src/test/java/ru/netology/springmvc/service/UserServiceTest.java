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
import ru.netology.springmvc.entity.Role;
import ru.netology.springmvc.entity.User;
import ru.netology.springmvc.exception.InvalidCredentials;
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

    private final User testUser = new User(1L, "test", "123", "test@test.com", Role.ROLE_USER);

    @Test
    void create() {
        when(userRepository.existsByUsername(testUser.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(testUser.getEmail())).thenReturn(false);
        when(userRepository.save(testUser)).thenReturn(testUser);

        User user = userService.create(testUser);
        assertEquals(testUser, user);
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    void create_userEmailExists() {
        when(userRepository.existsByEmail(testUser.getEmail())).thenReturn(true);
        assertThrows(InvalidCredentials.class, () -> userService.create(testUser));
    }

    @Test
    void create_userNameExists() {
        when(userRepository.existsByUsername(testUser.getUsername())).thenReturn(true);
        assertThrows(InvalidCredentials.class, () -> userService.create(testUser));
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
        String username = "notExists";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());
        assertThrows(InvalidCredentials.class, () -> userService.getByUsername(username));
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
    }

    @Test
    void loadUserByUsername() {
        when(userRepository.findByUsername(testUser.getUsername())).thenReturn(Optional.of(testUser));
        UserDetails userDetails = userService.loadUserByUsername(testUser.getUsername());
        assertEquals(testUser.getUsername(), userDetails.getUsername());
        verify(userRepository, times(1)).findByUsername(testUser.getUsername());
    }
}


