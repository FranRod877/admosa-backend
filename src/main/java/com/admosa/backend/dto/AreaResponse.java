package com.admosa.backend.dto;

import com.admosa.backend.domain.Area;

public record AreaResponse(Long id, String nombre, Long gerenteId, String gerenteNombre) {

    public static AreaResponse from(Area area) {
        return new AreaResponse(
                area.getId(),
                area.getNombre(),
                area.getGerente() != null ? area.getGerente().getId() : null,
                area.getGerente() != null ? area.getGerente().getNombre() : null);
    }
}
