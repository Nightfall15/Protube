package com.tecnocampus.LS2.protube_back.controller;

import com.tecnocampus.LS2.protube_back.models.User;
import com.tecnocampus.LS2.protube_back.services.AuthService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    AuthService authService;

    @InjectMocks
    AuthController authController;

    @Test
    void testRegister() {
        User user = new User();
        user.setUsername("pepito47");
        user.setPassword("megustaelpan");

        when(authService.register(user)).thenReturn("token123");

        ResponseEntity<String> response = authController.register(user);

        assertNotNull(response);
        assertEquals("token123", response.getBody());
        verify(authService, times(1)).register(user);
    }

    @Test
    void testLogin() {
        when(authService.login("gyatt", "mepicalacabeza")).thenReturn("jwt-token");

        AuthController.LoginRequest request = new AuthController.LoginRequest("gyatt", "mepicalacabeza");
        ResponseEntity<String> response = authController.login(request);

        assertNotNull(response);
        assertEquals("jwt-token", response.getBody());
        verify(authService, times(1)).login("gyatt", "mepicalacabeza");
    }
}