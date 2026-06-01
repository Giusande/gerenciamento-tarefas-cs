package com.backend.task_ger.dto;

import java.util.List;

import com.backend.task_ger.model.Prioridade;
import com.backend.task_ger.model.Status;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;

@Data
public class TarefaRequestDTO {
    @NotBlank
    private String titulo;
    private String descricao;
    private Status status;
    private Prioridade prioridade;
    private Long projetoId;
    private boolean restrita;
    private String senha;
    private List<Long> etiquetasIds;
}
