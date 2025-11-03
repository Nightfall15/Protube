package com.tecnocampus.LS2.protube_back.controller;

import com.tecnocampus.LS2.protube_back.models.User;
import com.tecnocampus.LS2.protube_back.services.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody User user) {
        String token = authService.register(user);
        return ResponseEntity.ok(token);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest request) {
        String token = authService.login(request.getUsername(), request.getPassword());
        return ResponseEntity.ok(token);
    }

    record LoginRequest(String username, String password) {

        public String getUsername() {
            return username;
        }
        public String getPassword() {
            return password;
        }
    }
}
