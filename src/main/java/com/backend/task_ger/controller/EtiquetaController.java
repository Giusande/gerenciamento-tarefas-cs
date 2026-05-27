package com.backend.task_ger.controller;

import com.backend.task_ger.dto.EtiquetaRequestDTO;
import com.backend.task_ger.model.Etiqueta;
import com.backend.task_ger.service.EtiquetaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import java.util.List;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/etiquetas")
@RequiredArgsConstructor
public class EtiquetaController {
    private final EtiquetaService service;

    @PostMapping
    public Etiqueta criar(@RequestBody @Valid EtiquetaRequestDTO dto) {
        return service.criar(dto);
    }

    @GetMapping
    public List<Etiqueta> listar() {
        return service.listar();
    }
}
