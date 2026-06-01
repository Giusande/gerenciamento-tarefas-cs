package com.backend.task_ger.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProjetoResponseDTO {
    private Long id;
    private String nome;
}
