package com.backend.task_ger.service;

import com.backend.task_ger.dto.*;
import com.backend.task_ger.exception.*;
import com.backend.task_ger.model.*;
import com.backend.task_ger.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import static com.backend.task_ger.repository.spec.TarefaSpecification.*;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TarefaService {
    private final TarefaRepository tarefaRepository;
    private final ProjetoRepository projetoRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final EtiquetaRepository etiquetaRepository;

    public TarefaResponseDTO criar(TarefaRequestDTO dto) {
        Usuario usuario = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Projeto projeto = projetoRepository.findById(dto.getProjetoId())
                .orElseThrow(() -> new NotFoundException("Projeto não encontrado"));

        if (!projeto.getUsuario().getId().equals(usuario.getId())) {
            throw new RuntimeException("Acesso negado");
        }

        Tarefa tarefa = Tarefa.builder()
                .titulo(dto.getTitulo())
                .descricao(dto.getDescricao())
                .status(dto.getStatus())
                .prioridade(dto.getPrioridade())
                .responsavel(usuario)
                .projeto(projeto)
                .restrita(dto.isRestrita())
                .senhaRestrita(dto.isRestrita() ? passwordEncoder.encode(dto.getSenha()) : null)
                .build();

        tarefaRepository.save(tarefa);

        return TarefaResponseDTO.builder()
                .id(tarefa.getId())
                .titulo(tarefa.getTitulo())
                .descricao(tarefa.getDescricao())
                .status(tarefa.getStatus().name())
                .prioridade(tarefa.getPrioridade().name())
                .projetoId(projeto.getId())
                .build();
    }
}
