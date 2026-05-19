package com.backend.task_ger.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comentario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O conteúdo é obrigatório")
    private String texto;

    @ManyToOne
    private Usuario autor;

    @JsonIgnore
    @ManyToOne
    private Tarefa tarefa;

    @CreationTimestamp
    private LocalDateTime criadoEm;
}
