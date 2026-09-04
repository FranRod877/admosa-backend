package com.admosa.backend.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record ArchivoResponse(
        UUID id,
        String nombreOriginal,
        String contentType,
        long tamanio,
        Long propietarioId,
        String propietarioNombre,
        String areaNombre,
        LocalDateTime fechaCarga,
        boolean puedeDescargar,
        boolean puedeEliminar) {
}
