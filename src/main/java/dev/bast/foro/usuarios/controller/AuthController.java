package dev.bast.foro.usuarios.controller;

import dev.bast.foro.common.exception.MessageResponse;
import dev.bast.foro.usuarios.dto.JwtResponse;
import dev.bast.foro.usuarios.dto.LoginRequest;
import dev.bast.foro.usuarios.dto.SignupRequest;
import dev.bast.foro.usuarios.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticación", description = "API para la autenticación y registro de usuarios")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(summary = "Iniciar sesión", description = "Autentica un usuario y devuelve un token JWT")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Autenticación exitosa", 
                    content = @Content(schema = @Schema(implementation = JwtResponse.class))),
        @ApiResponse(responseCode = "401", description = "Credenciales inválidas", 
                    content = @Content)
    })
    @PostMapping("/signin")
    public ResponseEntity<JwtResponse> authenticateUser(
            @Parameter(description = "Credenciales de inicio de sesión", required = true)
            @Valid @RequestBody LoginRequest loginRequest) {
        JwtResponse jwtResponse = authService.authenticateUser(loginRequest);
        return ResponseEntity.ok(jwtResponse);
    }

    @Operation(summary = "Registrarse", description = "Registra un nuevo usuario en el sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuario registrado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Error en los datos de registro. El nombre de usuario o email ya existen")
    })
    @PostMapping("/signup")
    public ResponseEntity<MessageResponse> registerUser(
            @Parameter(description = "Datos de registro del usuario", required = true)
            @Valid @RequestBody SignupRequest signUpRequest) {
        String response = authService.registerUser(signUpRequest);
        return ResponseEntity.ok(new MessageResponse(response));
    }
}