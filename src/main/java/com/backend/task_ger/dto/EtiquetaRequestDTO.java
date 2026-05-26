package com.backend.task_ger.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

@Data
public class EtiquetaRequestDTO {
    @NotBlank
    private String nome;
    private String cor;
}
