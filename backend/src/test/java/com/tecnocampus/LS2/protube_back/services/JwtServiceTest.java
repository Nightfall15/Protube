package com.tecnocampus.LS2.protube_back.services;

import com.tecnocampus.LS2.protube_back.models.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    JwtService jwtService = new JwtService();

    private User createUser() {
        User u = new User();
        u.setUsername("trobbio");
        u.setPassword("coolestbug");
        return u;
    }

    @Test
    void generateToken_ShouldReturnValidJwt() {
        User user = createUser();

        String token = jwtService.generateToken(user);

        assertNotNull(token);
        assertFalse(token.isEmpty());

        String username = jwtService.extractUsername(token);
        assertEquals("trobbio", username);
    }

    @Test
    void extractClaim_ShouldReturnCorrectClaim() {
        User user = createUser();
        String token = jwtService.generateToken(user);

        String subject = jwtService.extractClaim(token, Claims::getSubject);

        assertEquals("trobbio", subject);
    }

    @Test
    void validateToken_ShouldReturnTrueForValidToken() {
        User user = createUser();
        String token = jwtService.generateToken(user);

        assertTrue(jwtService.validateToken(token, user));
    }

    @Test
    void validateToken_ShouldReturnFalse_WhenUsernameDoesNotMatch() {
        User user = createUser();
        String token = jwtService.generateToken(user);

        User differentUser = new User();
        differentUser.setUsername("seth");

        assertFalse(jwtService.validateToken(token, differentUser));
    }
}
