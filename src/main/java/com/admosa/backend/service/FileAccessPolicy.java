package com.admosa.backend.service;

import com.admosa.backend.domain.Archivo;
import com.admosa.backend.domain.Area;
import com.admosa.backend.domain.Usuario;
import com.admosa.backend.repository.AreaRepository;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Centraliza las reglas de alcance por rol/área definidas en la especificación:
 * usuario estándar ve solo lo suyo, jefe de área ve su área, gerente ve las áreas
 * que gestiona, administrador ve todo.
 */
@Component
@RequiredArgsConstructor
public class FileAccessPolicy {

    private final AreaRepository areaRepository;

    public boolean canView(Usuario actor, Archivo archivo) {
        return switch (actor.getRol()) {
            case ADMINISTRADOR -> true;
            case GERENTE -> isOwner(actor, archivo) || isInManagedAreas(actor, archivo);
            case JEFE_AREA -> isOwner(actor, archivo) || isSameArea(actor, archivo);
            case USUARIO_ESTANDAR -> isOwner(actor, archivo);
        };
    }

    public boolean canDownload(Usuario actor, Archivo archivo) {
        return canView(actor, archivo);
    }

    public boolean canDelete(Usuario actor, Archivo archivo) {
        return switch (actor.getRol()) {
            case ADMINISTRADOR -> true;
            case GERENTE -> isOwner(actor, archivo) || isInManagedAreas(actor, archivo);
            case JEFE_AREA -> isOwner(actor, archivo);
            case USUARIO_ESTANDAR -> isOwner(actor, archivo);
        };
    }

    public List<Long> managedAreaIds(Usuario actor) {
        return areaRepository.findByGerenteId(actor.getId()).stream()
                .map(Area::getId)
                .collect(Collectors.toList());
    }

    private boolean isOwner(Usuario actor, Archivo archivo) {
        return Objects.equals(actor.getId(), archivo.getPropietario().getId());
    }

    private boolean isSameArea(Usuario actor, Archivo archivo) {
        return actor.getArea() != null
                && archivo.getArea() != null
                && Objects.equals(actor.getArea().getId(), archivo.getArea().getId());
    }

    private boolean isInManagedAreas(Usuario actor, Archivo archivo) {
        if (archivo.getArea() == null) {
            return false;
        }
        return managedAreaIds(actor).contains(archivo.getArea().getId());
    }
}
