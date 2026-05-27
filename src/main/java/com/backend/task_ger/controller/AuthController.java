package com.backend.task_ger.controller;

import com.backend.task_ger.dto.LoginRequestDTO;
import com.backend.task_ger.dto.LoginResponseDTO;
import com.backend.task_ger.dto.UsuarioRequestDTO;
import com.backend.task_ger.dto.UsuarioResponseDTO;
import com.backend.task_ger.model.Usuario;
import com.backend.task_ger.security.JwtService;
import com.backend.task_ger.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UsuarioService service;
    private final JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<UsuarioResponseDTO> register(@RequestBody @Valid UsuarioRequestDTO dto) {
        return ResponseEntity.ok(service.criarUsuario(dto));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody @Valid LoginRequestDTO dto) {
        Usuario usuario = service.validarLogin(dto.getEmail(), dto.getSenha());
        String token = jwtService.gerarToken(usuario.getEmail());
        return ResponseEntity.ok(LoginResponseDTO.builder().token(token).build());
    }

}
