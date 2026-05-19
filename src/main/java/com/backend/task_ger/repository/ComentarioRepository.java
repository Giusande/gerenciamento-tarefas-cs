package com.backend.task_ger.repository;

import com.backend.task_ger.model.Comentario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ComentarioRepository extends JpaRepository<Comentario, Long> {
    List<Comentario> findByTarefaId(Long tarefaId);
}
