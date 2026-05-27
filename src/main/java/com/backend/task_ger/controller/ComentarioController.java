package com.backend.task_ger.controller;

import com.backend.task_ger.dto.ComentarioRequestDTO;
import com.backend.task_ger.model.Comentario;
import com.backend.task_ger.service.ComentarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/comentarios")
@RequiredArgsConstructor
public class ComentarioController {
    private final ComentarioService service;

    @PostMapping
    public Comentario criar(@RequestBody @Valid ComentarioRequestDTO dto) {
        return service.criar(dto);
    }

    @GetMapping("/tarefa/{id}")
    public List<Comentario> listar(@PathVariable Long id) {
        return service.listar(id);
    }
}
