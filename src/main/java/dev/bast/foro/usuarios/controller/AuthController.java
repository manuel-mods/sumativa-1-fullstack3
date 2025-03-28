package dev.bast.foro.usuarios.controller;

import dev.bast.foro.usuarios.dto.JwtResponse;
import dev.bast.foro.usuarios.dto.LoginRequest;
import dev.bast.foro.usuarios.dto.SignupRequest;
import dev.bast.foro.usuarios.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signin")
    public ResponseEntity<JwtResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        JwtResponse jwtResponse = authService.authenticateUser(loginRequest);
        return ResponseEntity.ok(jwtResponse);
    }

    @PostMapping("/signup")
    public ResponseEntity<String> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        String response = authService.registerUser(signUpRequest);
        return ResponseEntity.ok(response);
    }
}