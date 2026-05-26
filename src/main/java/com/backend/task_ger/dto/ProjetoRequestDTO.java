package com.backend.task_ger.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ProjetoRequestDTO {
    @NotBlank
    private String nome;
}
