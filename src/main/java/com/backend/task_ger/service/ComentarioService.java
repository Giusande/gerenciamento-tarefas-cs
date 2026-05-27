package com.backend.task_ger.service;

import com.backend.task_ger.dto.ComentarioRequestDTO;
import com.backend.task_ger.exception.NotFoundException;
import com.backend.task_ger.model.*;
import com.backend.task_ger.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ComentarioService {
    private final ComentarioRepository comentarioRepository;
    private final TarefaRepository tarefaRepository;

    public Comentario criar(ComentarioRequestDTO dto) {
        Usuario usuario = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Tarefa tarefa = tarefaRepository.findById(dto.getTarefaId())
                .orElseThrow(() -> new NotFoundException("Tarefa não encontrada"));

        Comentario comentario = Comentario.builder()
                .texto(dto.getTexto())
                .tarefa(tarefa)
                .autor(usuario)
                .build();

        return comentarioRepository.save(comentario);
    }

    public List<Comentario> listar(Long tarefaId) {
        return comentarioRepository.findByTarefaId(tarefaId);
    }
}
