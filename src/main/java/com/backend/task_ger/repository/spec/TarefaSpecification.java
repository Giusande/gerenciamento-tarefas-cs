package com.backend.task_ger.repository.spec;

import com.backend.task_ger.model.*;
import org.springframework.data.jpa.domain.Specification;

public class TarefaSpecification {
    public static Specification<Tarefa> comStatus(Status status) {
        return (root, query, cb) -> status == null ? null : cb.equal(root.get("status"), status);
    }

    public static Specification<Tarefa> comPrioridade(Prioridade prioridade) {
        return (root, query, cb) -> prioridade == null ? null : cb.equal(root.get("prioridade"), prioridade);
    }

    public static Specification<Tarefa> porUsuario(Long usuarioId) {
        return (root, query, cb) -> usuarioId == null ? null : cb.equal(root.get("responsavel").get("id"), usuarioId);
    }
}
