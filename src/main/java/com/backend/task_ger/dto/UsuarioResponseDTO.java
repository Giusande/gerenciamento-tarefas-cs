package com.backend.task_ger.dto;

import lombok.Data;
import lombok.Builder;

@Data
@Builder
public class UsuarioResponseDTO {
    private Long id;
    private String nome;
    private String email;
}
