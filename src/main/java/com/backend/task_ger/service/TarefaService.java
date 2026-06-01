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
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TarefaService {
    private final TarefaRepository tarefaRepository;
    private final ProjetoRepository projetoRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final EtiquetaRepository etiquetaRepository;

    public TarefaResponseDTO criar(TarefaRequestDTO dto) {

        Usuario usuario =
                (Usuario) SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getPrincipal();

        Projeto projeto =
                projetoRepository.findById(
                        dto.getProjetoId()
                )
                .orElseThrow(() ->
                        new NotFoundException(
                                "Projeto não encontrado"
                        ));

        if (!projeto.getUsuario()
                .getId()
                .equals(usuario.getId())) {

            throw new RuntimeException(
                    "Acesso negado"
            );
        }

        Tarefa tarefa = Tarefa.builder()
                .titulo(dto.getTitulo())
                .descricao(dto.getDescricao())
                .status(dto.getStatus())
                .prioridade(dto.getPrioridade())
                .responsavel(usuario)
                .projeto(projeto)
                .restrita(dto.isRestrita())
                .senhaRestrita(
                        dto.isRestrita()
                                ? passwordEncoder.encode(
                                        dto.getSenha()
                                )
                                : null
                )
                .build();

        if (dto.getEtiquetasIds() != null &&
            !dto.getEtiquetasIds().isEmpty()) {

            List<Etiqueta> etiquetas =
                    etiquetaRepository.findAllById(
                            dto.getEtiquetasIds()
                    );

            tarefa.setEtiquetas(etiquetas);
        }

        tarefaRepository.save(tarefa);

        return TarefaResponseDTO.builder()
                .id(tarefa.getId())
                .titulo(tarefa.getTitulo())
                .descricao(tarefa.getDescricao())
                .status(tarefa.getStatus().name())
                .prioridade(tarefa.getPrioridade().name())
                .bloqueada(tarefa.isRestrita())
                .etiquetas(tarefa.getEtiquetas())
                .build();
    }

    public Page<TarefaResponseDTO> listar(Status status, Prioridade prioridade, Pageable pageable) {
        Usuario usuario = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Specification<Tarefa> spec = Specification.where(comStatus(status)).and(comPrioridade(prioridade)).and(porUsuario(usuario.getId()));

        return tarefaRepository.findAll(spec, pageable).map(tarefa -> TarefaResponseDTO.builder()
                .id(tarefa.getId())
                .titulo(tarefa.isRestrita() ? "🔒 Tarefa Restrita" : tarefa.getTitulo())
                .descricao(tarefa.isRestrita() ? null : tarefa.getDescricao())
                .status(tarefa.getStatus().name())
                .prioridade(tarefa.getPrioridade().name())
                .bloqueada(tarefa.isRestrita())
                .etiquetas(tarefa.getEtiquetas())
                .build());
    }

    public Page<TarefaResponseDTO> listarPorProjeto(Long projetoId,Pageable pageable) {

        return tarefaRepository.findByProjetoId(projetoId, pageable).map(tarefa -> TarefaResponseDTO.builder()
                .id(tarefa.getId())
                .titulo(tarefa.isRestrita() ? "🔒 Tarefa Restrita" : tarefa.getTitulo())
                .descricao(tarefa.isRestrita() ? null : tarefa.getDescricao())
                .status(tarefa.getStatus().name())
                .prioridade(tarefa.getPrioridade().name())
                .bloqueada(tarefa.isRestrita())
                .etiquetas(tarefa.getEtiquetas())
                .build());
    }

    public TarefaResponseDTO desbloquear(Long tarefaId, DesbloqueioRequestDTO dto) {
        Tarefa tarefa = tarefaRepository.findById(tarefaId)
                .orElseThrow(() -> new NotFoundException("Tarefa não encontrada"));

        if (!tarefa.isRestrita()) {
            throw new RuntimeException("Tarefa não é restrita");
        }

        boolean senhaCorreta = passwordEncoder.matches(dto.getSenha(), tarefa.getSenhaRestrita());

        if (!senhaCorreta) {
            throw new RuntimeException("Senha incorreta");
        }

        return TarefaResponseDTO.builder()
                .id(tarefa.getId())
                .titulo(tarefa.getTitulo())
                .descricao(tarefa.getDescricao())
                .status(tarefa.getStatus().name())
                .prioridade(tarefa.getPrioridade().name())
                .bloqueada(false)
                .etiquetas(tarefa.getEtiquetas())
                .build();
    }

    public void deletar(Long id) {
        Tarefa tarefa = tarefaRepository.findById(id).orElseThrow(() -> new NotFoundException("Tarefa não encontrada!"));
        tarefaRepository.delete(tarefa);
    }

    public TarefaResponseDTO atualizarParcial(Long id, TarefaPatchDTO dto) {    
        Tarefa tarefa = tarefaRepository.findById(id).orElseThrow(() -> new NotFoundException("Tarefa não encontrada!"));

        if (dto.getTitulo() != null) {
            tarefa.setTitulo(dto.getTitulo());
        }

        if (dto.getDescricao() != null) {
            tarefa.setDescricao(dto.getDescricao());
        }

        if (dto.getStatus() != null) {
            tarefa.setStatus(dto.getStatus());
            if (dto.getStatus() == Status.CONCLUIDA) {
                tarefa.setConcluidaEm(LocalDateTime.now());
            }
        }

        if (dto.getPrioridade() != null) {
            tarefa.setPrioridade(dto.getPrioridade());
        }

        if (dto.getEtiquetasIds() != null) {
            List<Etiqueta> etiquetas = etiquetaRepository.findAllById(dto.getEtiquetasIds());
            tarefa.setEtiquetas(etiquetas);
        }

        tarefaRepository.save(tarefa);

        return TarefaResponseDTO.builder()
                .id(tarefa.getId())
                .titulo(tarefa.getTitulo())
                .descricao(tarefa.getDescricao())
                .status(tarefa.getStatus().name())
                .prioridade(tarefa.getPrioridade().name())
                .bloqueada(tarefa.isRestrita())
                .etiquetas(tarefa.getEtiquetas())
                .build();
    }

    public Page<TarefaResponseDTO> historico(Pageable pageable) {
        Usuario usuario = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return tarefaRepository.findByResponsavelIdAndStatusOrderByConcluidaEmDesc(usuario.getId(), Status.CONCLUIDA, pageable)
                .map(tarefa -> TarefaResponseDTO.builder()
                        .id(tarefa.getId())
                        .titulo(tarefa.getTitulo())
                        .descricao(tarefa.getDescricao())
                        .status(tarefa.getStatus().name())
                        .prioridade(tarefa.getPrioridade().name())
                        .bloqueada(tarefa.isRestrita())
                        .etiquetas(tarefa.getEtiquetas())
                        .build());
    }
}