package com.backend.task_ger.dto;

import java.time.LocalDateTime;
import java.util.List;
import com.backend.task_ger.model.Etiqueta;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TarefaResponseDTO {
    private Long id; 
    private String titulo;
    private String descricao;
    private String status;
    private String prioridade;
    private boolean bloqueada;
    private LocalDateTime concluidaEm;
    private List<Etiqueta> etiquetas;
}
