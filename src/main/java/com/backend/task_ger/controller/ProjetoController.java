package com.backend.task_ger.controller;

import com.backend.task_ger.model.Projeto;
import com.backend.task_ger.dto.ProjetoRequestDTO;
import com.backend.task_ger.dto.ProjetoResponseDTO;
import com.backend.task_ger.service.ProjetoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.*;

@RestController
@RequestMapping("/projetos")
@RequiredArgsConstructor
public class ProjetoController {
    private final ProjetoService service;

    @PostMapping
    public Projeto criar(@RequestBody @Valid ProjetoRequestDTO dto) {
        return service.criar(dto);
    }

    @GetMapping
    public ResponseEntity<Page<ProjetoResponseDTO>> listar(Pageable pageable) {
        return ResponseEntity.ok(service.listar(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjetoResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }
 
}
