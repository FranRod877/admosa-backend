package com.admosa.backend.service;

import com.admosa.backend.domain.Area;
import com.admosa.backend.domain.Rol;
import com.admosa.backend.domain.Usuario;
import com.admosa.backend.dto.AreaResponse;
import com.admosa.backend.exception.ResourceNotFoundException;
import com.admosa.backend.repository.AreaRepository;
import com.admosa.backend.repository.UsuarioRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AreaService {

    private final AreaRepository areaRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional(readOnly = true)
    public List<AreaResponse> listAll() {
        return areaRepository.findAll().stream()
                .map(AreaResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public AreaResponse updateGerente(Long areaId, Long gerenteId) {
        Area area = areaRepository.findById(areaId)
                .orElseThrow(() -> new ResourceNotFoundException("Área no encontrada"));

        if (gerenteId == null) {
            area.setGerente(null);
        } else {
            Usuario gerente = usuarioRepository.findById(gerenteId)
                    .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
            if (gerente.getRol() != Rol.GERENTE) {
                throw new IllegalArgumentException("El usuario debe tener rol GERENTE");
            }
            area.setGerente(gerente);
        }

        return AreaResponse.from(areaRepository.save(area));
    }
}
