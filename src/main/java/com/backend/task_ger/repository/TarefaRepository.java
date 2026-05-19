package com.backend.task_ger.repository;

import com.backend.task_ger.model.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TarefaRepository extends JpaRepository<Tarefa, Long>, JpaSpecificationExecutor<Tarefa> {
    Page<Tarefa> findByProjetoId(Long projetoId, Pageable pageable);

    Page<Tarefa> findByPrioridade(Prioridade prioridade, Pageable pageable);

    Page<Tarefa> findByResponsavelId(Long usuarioId, Pageable pageable);

    Page<Tarefa> findByResponsavelIdAndStatusOrderByConcluidaEmDesc(Long usuarioId, Status status, Pageable pageable);
}
