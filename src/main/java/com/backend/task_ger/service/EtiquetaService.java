package com.backend.task_ger.service;

import com.backend.task_ger.model.Etiqueta;
import com.backend.task_ger.repository.EtiquetaRepository;
import com.backend.task_ger.dto.EtiquetaRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EtiquetaService {
    private final EtiquetaRepository etiquetaRepository;

    public Etiqueta criar(EtiquetaRequestDTO dto) {
        Etiqueta etiqueta = Etiqueta.builder()
                .nome(dto.getNome())
                .cor(dto.getCor())
                .build();

        return etiquetaRepository.save(etiqueta);
    }

    public List<Etiqueta> listar() {
        return etiquetaRepository.findAll();
    }
}
