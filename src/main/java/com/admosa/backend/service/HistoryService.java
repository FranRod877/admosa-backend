package com.admosa.backend.service;

import com.admosa.backend.domain.HistorialAccion;
import com.admosa.backend.domain.Usuario;
import com.admosa.backend.dto.HistorialResponse;
import com.admosa.backend.repository.HistorialAccionRepository;
import com.admosa.backend.repository.UsuarioRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Acceso al historial reservado a gerente y administrador: el gerente ve las
 * acciones de los usuarios de las áreas que gestiona, el administrador ve todo.
 */
@Service
@RequiredArgsConstructor
public class HistoryService {

    private final HistorialAccionRepository historialAccionRepository;
    private final UsuarioRepository usuarioRepository;
    private final FileAccessPolicy accessPolicy;

    @Transactional(readOnly = true)
    public List<HistorialResponse> historialPara(Usuario actor) {
        List<HistorialAccion> registros = switch (actor.getRol()) {
            case ADMINISTRADOR -> historialAccionRepository.findAllByOrderByFechaDesc();
            case GERENTE -> {
                List<Long> areaIds = accessPolicy.managedAreaIds(actor);
                List<Long> usuarioIds = areaIds.stream()
                        .flatMap(areaId -> usuarioRepository.findByAreaId(areaId).stream())
                        .map(Usuario::getId)
                        .distinct()
                        .collect(Collectors.toCollection(ArrayList::new));
                usuarioIds.add(actor.getId());
                yield historialAccionRepository.findByUsuarioIdInOrderByFechaDesc(usuarioIds);
            }
            case JEFE_AREA, USUARIO_ESTANDAR -> throw new AccessDeniedException("No tienes permiso para ver el historial");
        };

        return registros.stream().map(this::toResponse).collect(Collectors.toList());
    }

    private HistorialResponse toResponse(HistorialAccion registro) {
        return new HistorialResponse(
                registro.getId(),
                registro.getUsuario().getId(),
                registro.getUsuario().getNombre(),
                registro.getArchivo() != null ? registro.getArchivo().getId() : null,
                registro.getDetalle(),
                registro.getAccion(),
                registro.getFecha());
    }
}
