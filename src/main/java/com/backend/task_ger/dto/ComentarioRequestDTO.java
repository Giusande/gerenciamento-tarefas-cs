package com.backend.task_ger.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ComentarioRequestDTO {
    @NotBlank
    private String texto;

    private Long tarefaId;
}
