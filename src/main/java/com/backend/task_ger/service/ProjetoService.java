package com.backend.task_ger.service;

import com.backend.task_ger.dto.ProjetoRequestDTO;
import com.backend.task_ger.dto.ProjetoResponseDTO;
import com.backend.task_ger.exception.NotFoundException;
import com.backend.task_ger.model.Projeto;
import com.backend.task_ger.model.Usuario;
import com.backend.task_ger.repository.ProjetoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.data.domain.*;

@Service
@RequiredArgsConstructor
public class ProjetoService {
    private final ProjetoRepository repository;

    public Projeto criar(ProjetoRequestDTO dto) {
        Usuario usuario = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Projeto projeto = Projeto.builder()
                .nome(dto.getNome())
                .usuario(usuario)
                .build();

        return repository.save(projeto);
    }

    public Page<ProjetoResponseDTO> listar(Pageable pageable) {
        return repository.findAll(pageable).map(projeto -> ProjetoResponseDTO.builder()
            .id(projeto.getId())
            .nome(projeto.getNome())
            .build()
        );
    }

    public ProjetoResponseDTO buscarPorId(Long Id){
        Projeto projeto = repository.findById(Id).orElseThrow(() -> new NotFoundException("Projeto não encontrado!"));

        return ProjetoResponseDTO.builder()
            .id(projeto.getId())
            .nome(projeto.getNome())
            .build();

    }

}
