package com.backend.task_ger.controller;

import com.backend.task_ger.dto.*;
import com.backend.task_ger.model.Prioridade;
import com.backend.task_ger.model.Status;
import com.backend.task_ger.service.TarefaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@RestController
@RequestMapping("/tarefas")
@RequiredArgsConstructor
public class TarefaController {
    private final TarefaService service;

    @PostMapping
    public ResponseEntity<TarefaResponseDTO> criar(@RequestBody @Valid TarefaRequestDTO dto) {
        return ResponseEntity.ok(service.criar(dto));
    }

    @GetMapping
    public ResponseEntity<Page<TarefaResponseDTO>> listar(@RequestParam(required = false) Status status, Prioridade prioridade, Pageable pageable) {
        return ResponseEntity.ok(service.listar(status, prioridade, pageable));
    }

    @PostMapping("/{id}/desbloquear")
    public ResponseEntity<TarefaResponseDTO> desbloquear(@PathVariable Long id, @RequestBody DesbloqueioRequestDTO dto) {
        return ResponseEntity.ok(service.desbloquear(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        service.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}")
    public ResponseEntity<TarefaResponseDTO> atualizarParcial(@PathVariable Long id, @RequestBody TarefaPatchDTO dto) {
        return ResponseEntity.ok(service.atualizarParcial(id, dto));
    }

    @GetMapping("/historico")
    public ResponseEntity<Page<TarefaResponseDTO>> historico(Pageable pageable) {
        return ResponseEntity.ok(service.historico(pageable));
    }

    @PostMapping("/{tarefaId}/etiquetas/{etiquetaId}")
    public ResponseEntity<TarefaResponseDTO> adicionarEtiqueta(@PathVariable Long tarefaId, @PathVariable Long etiquetaId) {
        service.adicionarEtiqueta(tarefaId, etiquetaId);
        return ResponseEntity.ok().build();
    }
}

