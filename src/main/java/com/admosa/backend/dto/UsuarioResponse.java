package com.admosa.backend.dto;

import com.admosa.backend.domain.Rol;
import com.admosa.backend.domain.Usuario;

public record UsuarioResponse(
        Long id,
        String nombre,
        String email,
        Rol rol,
        Long areaId,
        String areaNombre) {

    public static UsuarioResponse from(Usuario usuario) {
        return new UsuarioResponse(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getEmail(),
                usuario.getRol(),
                usuario.getArea() != null ? usuario.getArea().getId() : null,
                usuario.getArea() != null ? usuario.getArea().getNombre() : null);
    }
}
