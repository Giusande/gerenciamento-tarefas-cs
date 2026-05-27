package com.backend.task_ger.controller;

import com.backend.task_ger.model.Projeto;
import com.backend.task_ger.dto.ProjetoRequestDTO;
import com.backend.task_ger.service.ProjetoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/projetos")
@RequiredArgsConstructor
public class ProjetoController {
    private final ProjetoService service;

    @PostMapping
    public Projeto criar(@RequestBody @Valid ProjetoRequestDTO dto) {
        return service.criar(dto);
    }
}
