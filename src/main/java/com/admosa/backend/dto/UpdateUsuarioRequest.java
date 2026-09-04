package com.admosa.backend.dto;

import com.admosa.backend.domain.Rol;

public record UpdateUsuarioRequest(Rol rol, Long areaId) {
}
