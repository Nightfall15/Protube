package com.tecnocampus.LS2.protube_back.services;

import com.tecnocampus.LS2.protube_back.models.User;
import com.tecnocampus.LS2.protube_back.repositories.IUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    private IUserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private JwtService jwtService;
    private AuthenticationManager authenticationManager;

    private AuthService authService;

    @BeforeEach
    void setup() {
        userRepository = mock(IUserRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        jwtService = mock(JwtService.class);
        authenticationManager = mock(AuthenticationManager.class);

        authService = new AuthService(
                userRepository,
                passwordEncoder,
                jwtService,
                authenticationManager
        );
    }

    @Test
    void testRegister() {
        User user = new User();
        user.setPassword("cocacola123");

        when(passwordEncoder.encode("cocacola123")).thenReturn("encoded");
        when(jwtService.generateToken(any(User.class))).thenReturn("jwt-token");

        String token = authService.register(user);

        ArgumentCaptor<User> savedUserCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(savedUserCaptor.capture());
        User savedUser = savedUserCaptor.getValue();

        assertEquals("encoded", savedUser.getPassword());
        assertEquals("jwt-token", token);
    }

    @Test
    void testLoginSuccess() {
        User user = new User();
        user.setUsername("wyrm");

        when(userRepository.findByUsername("wyrm")).thenReturn(Optional.of(user));
        when(jwtService.generateToken(user)).thenReturn("jwt123");

        String token = authService.login("wyrm", "password123");

        // verify authenticationManager is called
        verify(authenticationManager).authenticate(
                new UsernamePasswordAuthenticationToken("wyrm", "password123")
        );

        assertEquals("jwt123", token);
    }

    @Test
    void testLoginUserNotFound() {
        when(userRepository.findByUsername("banker_millibelle")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () ->
                authService.login("banker_millibelle", "pass")
        );
    }
}
