package com.admosa.backend.service;

import com.admosa.backend.domain.Area;
import com.admosa.backend.domain.Rol;
import com.admosa.backend.domain.Usuario;
import com.admosa.backend.dto.UpdateUsuarioRequest;
import com.admosa.backend.dto.UsuarioResponse;
import com.admosa.backend.exception.ResourceNotFoundException;
import com.admosa.backend.repository.AreaRepository;
import com.admosa.backend.repository.UsuarioRepository;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    /** Orden de despliegue en el panel de administración: de mayor a menor jerarquía. */
    private static final List<Rol> ORDEN_ROLES =
            List.of(Rol.ADMINISTRADOR, Rol.GERENTE, Rol.JEFE_AREA, Rol.USUARIO_ESTANDAR);

    private final UsuarioRepository usuarioRepository;
    private final AreaRepository areaRepository;

    @Transactional(readOnly = true)
    public List<UsuarioResponse> listAll() {
        return usuarioRepository.findAll().stream()
                .sorted(Comparator.<Usuario>comparingInt(u -> ORDEN_ROLES.indexOf(u.getRol()))
                        .thenComparing(Usuario::getNombre))
                .map(UsuarioResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public UsuarioResponse update(Long id, UpdateUsuarioRequest request) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        if (request.rol() != null) {
            usuario.setRol(request.rol());
        }

        boolean sinArea = usuario.getRol() == Rol.ADMINISTRADOR || usuario.getRol() == Rol.GERENTE;
        if (sinArea) {
            usuario.setArea(null);
        } else if (request.areaId() != null) {
            Area area = areaRepository.findById(request.areaId())
                    .orElseThrow(() -> new ResourceNotFoundException("Área no encontrada"));
            usuario.setArea(area);
        }

        return UsuarioResponse.from(usuarioRepository.save(usuario));
    }
}
