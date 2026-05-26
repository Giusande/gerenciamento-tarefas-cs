package com.backend.task_ger.dto;

import lombok.Data;
import lombok.Builder;

@Data  
@Builder
public class LoginResponseDTO {
    private String token;
}