package co.analisys.biblioteca.controller;

import co.analisys.biblioteca.model.Email;
import co.analisys.biblioteca.model.Usuario;
import co.analisys.biblioteca.model.UsuarioId;
import co.analisys.biblioteca.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/usuarios")
@Tag(name = "Usuarios", description = "APIs para la gestión de usuarios del sistema")
public class UsuarioController {
    @Autowired
    private UsuarioService usuarioService;

    @Operation(summary = "Obtener usuario por ID", description = "Recupera la información completa de un usuario utilizando su identificador único.", security = @SecurityRequirement(name = "Bearer Authentication"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario encontrado exitosamente", content = @Content(schema = @Schema(implementation = Usuario.class))),
            @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content),
            @ApiResponse(responseCode = "403", description = "No autorizado", content = @Content),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado", content = @Content)
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_LIBRARIAN', 'ROLE_USER')")
    public Usuario obtenerUsuario(
            @Parameter(description = "ID del usuario a buscar", required = true, example = "1") @PathVariable String id) {
        return usuarioService.obtenerUsuario(new UsuarioId(id));
    }

    @Operation(summary = "Cambiar email de usuario", description = "Actualiza la dirección de correo electrónico de un usuario. Los usuarios pueden actualizar su propio email, y los bibliotecarios pueden actualizar el email de cualquier usuario.", security = @SecurityRequirement(name = "Bearer Authentication"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Email actualizado exitosamente", content = @Content),
            @ApiResponse(responseCode = "400", description = "Email inválido", content = @Content),
            @ApiResponse(responseCode = "401", description = "No autenticado", content = @Content),
            @ApiResponse(responseCode = "403", description = "No autorizado", content = @Content),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado", content = @Content)
    })
    @PutMapping("/{id}/email")
    @PreAuthorize("hasAnyRole('ROLE_LIBRARIAN', 'ROLE_USER')")
    public void cambiarEmail(
            @Parameter(description = "ID del usuario a actualizar", required = true, example = "1") @PathVariable String id,
            @Parameter(description = "Nueva dirección de correo electrónico", required = true, content = @Content(schema = @Schema(type = "string", example = "nuevo.email@ejemplo.com"))) @RequestBody String nuevoEmail) {
        usuarioService.cambiarEmailUsuario(new UsuarioId(id), new Email(nuevoEmail));
    }

    @Operation(summary = "Estado del servicio", description = "Endpoint público para verificar el estado del servicio de usuarios. No requiere autenticación.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Servicio disponible", content = @Content(schema = @Schema(implementation = String.class)))
    })
    @GetMapping("/public/status")
    public String getPublicStatus() {
        return "El servicio de usuarios está funcionando correctamente";
    }
}
