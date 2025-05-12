package dev.bast.foro.foros.controller;

import dev.bast.foro.foros.dto.TopicDto;
import dev.bast.foro.foros.service.TopicService;
import dev.bast.foro.usuarios.security.services.UserDetailsImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/topics")
@Tag(name = "Temas", description = "API para la gestión de temas del foro")
public class TopicController {
    
    private final TopicService topicService;

    public TopicController(TopicService topicService) {
        this.topicService = topicService;
    }

    @Operation(summary = "Obtener todos los temas", description = "Devuelve una lista con todos los temas del foro")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de temas recuperada con éxito",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = TopicDto.class))))
    })
    @GetMapping
    public ResponseEntity<List<TopicDto>> getAllTopics() {
        List<TopicDto> topics = topicService.getAllTopics();
        return ResponseEntity.ok(topics);
    }

    @Operation(summary = "Obtener tema por ID", description = "Devuelve un tema específico según su ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Tema encontrado",
                    content = @Content(schema = @Schema(implementation = TopicDto.class))),
        @ApiResponse(responseCode = "404", description = "Tema no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<TopicDto> getTopicById(
            @Parameter(description = "ID del tema", required = true)
            @PathVariable Long id) {
        TopicDto topic = topicService.getTopicById(id);
        return ResponseEntity.ok(topic);
    }

    @Operation(summary = "Crear un nuevo tema", description = "Crea un nuevo tema en el foro",
              security = @SecurityRequirement(name = "Bearer Authentication"))
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Tema creado con éxito",
                    content = @Content(schema = @Schema(implementation = TopicDto.class))),
        @ApiResponse(responseCode = "400", description = "Datos de tema inválidos"),
        @ApiResponse(responseCode = "401", description = "No autorizado")
    })
    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<TopicDto> createTopic(
            @Parameter(description = "Datos del nuevo tema", required = true)
            @Valid @RequestBody TopicDto topicDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        
        TopicDto createdTopic = topicService.createTopic(
                topicDto, 
                userDetails.getId(), 
                userDetails.getUsername()
        );
        
        return new ResponseEntity<>(createdTopic, HttpStatus.CREATED);
    }

    @Operation(summary = "Actualizar un tema", description = "Actualiza un tema existente",
              security = @SecurityRequirement(name = "Bearer Authentication"))
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Tema actualizado con éxito"),
        @ApiResponse(responseCode = "400", description = "Datos de tema inválidos"),
        @ApiResponse(responseCode = "401", description = "No autorizado"),
        @ApiResponse(responseCode = "403", description = "Prohibido - No tiene permisos para modificar este tema"),
        @ApiResponse(responseCode = "404", description = "Tema no encontrado")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<TopicDto> updateTopic(
            @Parameter(description = "ID del tema a actualizar", required = true)
            @PathVariable Long id,
            @Parameter(description = "Datos actualizados del tema", required = true)
            @Valid @RequestBody TopicDto topicDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        
        TopicDto existingTopic = topicService.getTopicById(id);
        
        // Only allow the creator or admin/moderator to update
        if (!existingTopic.getUserId().equals(userDetails.getId()) && 
                !authentication.getAuthorities().stream()
                        .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || 
                                       a.getAuthority().equals("ROLE_MODERATOR"))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        TopicDto updatedTopic = topicService.updateTopic(id, topicDto);
        return ResponseEntity.ok(updatedTopic);
    }

    @Operation(summary = "Eliminar un tema", description = "Elimina un tema existente",
              security = @SecurityRequirement(name = "Bearer Authentication"))
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Tema eliminado con éxito"),
        @ApiResponse(responseCode = "401", description = "No autorizado"),
        @ApiResponse(responseCode = "403", description = "Prohibido - No tiene permisos para eliminar este tema"),
        @ApiResponse(responseCode = "404", description = "Tema no encontrado")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<Void> deleteTopic(
            @Parameter(description = "ID del tema a eliminar", required = true)
            @PathVariable Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        
        TopicDto existingTopic = topicService.getTopicById(id);
        
        // Only allow the creator or admin/moderator to delete
        if (!existingTopic.getUserId().equals(userDetails.getId()) && 
                !authentication.getAuthorities().stream()
                        .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || 
                                       a.getAuthority().equals("ROLE_MODERATOR"))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        topicService.deleteTopic(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Banear un tema", description = "Marca un tema como baneado (solo moderadores y administradores)",
              security = @SecurityRequirement(name = "Bearer Authentication"))
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Tema baneado con éxito"),
        @ApiResponse(responseCode = "401", description = "No autorizado"),
        @ApiResponse(responseCode = "403", description = "Prohibido - No tiene rol de moderador o administrador"),
        @ApiResponse(responseCode = "404", description = "Tema no encontrado")
    })
    @PutMapping("/{id}/ban")
    @PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
    public ResponseEntity<Void> banTopic(
            @Parameter(description = "ID del tema a banear", required = true)
            @PathVariable Long id) {
        topicService.banTopic(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Obtener temas por usuario", description = "Devuelve una lista de temas creados por un usuario específico")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de temas recuperada con éxito",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = TopicDto.class))))
    })
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<TopicDto>> getTopicsByUserId(
            @Parameter(description = "ID del usuario", required = true)
            @PathVariable Long userId) {
        List<TopicDto> topics = topicService.getTopicsByUserId(userId);
        return ResponseEntity.ok(topics);
    }
}