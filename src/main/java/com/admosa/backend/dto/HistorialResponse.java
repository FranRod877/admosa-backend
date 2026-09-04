package com.admosa.backend.dto;

import com.admosa.backend.domain.AccionHistorial;
import java.time.LocalDateTime;
import java.util.UUID;

public record HistorialResponse(
        Long id,
        Long usuarioId,
        String usuarioNombre,
        UUID archivoId,
        String archivoNombre,
        AccionHistorial accion,
        LocalDateTime fecha) {
}
