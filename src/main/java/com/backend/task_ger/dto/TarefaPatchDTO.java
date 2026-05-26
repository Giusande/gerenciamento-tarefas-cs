package com.backend.task_ger.dto;

import java.util.List;
import com.backend.task_ger.model.Prioridade;
import com.backend.task_ger.model.Status;
import lombok.Data;

@Data
public class TarefaPatchDTO {
    private String titulo;
    private String descricao;
    private Status status;
    private Prioridade prioridade;
    private List<String> etiquetasIds;
}
