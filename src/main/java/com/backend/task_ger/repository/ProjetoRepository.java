package com.backend.task_ger.repository;

import com.backend.task_ger.model.Projeto;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProjetoRepository extends JpaRepository<Projeto, Long> {
    List<Projeto> findByUsuarioId(String usuarioId);
}
