package com.backend.task_ger.exception;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class ErrorResponse {
    private int status;
    private String erro;
    private LocalDateTime data;
}
