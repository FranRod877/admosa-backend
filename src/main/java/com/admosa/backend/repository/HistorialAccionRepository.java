package com.admosa.backend.repository;

import com.admosa.backend.domain.HistorialAccion;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HistorialAccionRepository extends JpaRepository<HistorialAccion, Long> {

    List<HistorialAccion> findAllByOrderByFechaDesc();

    List<HistorialAccion> findByUsuarioIdOrderByFechaDesc(Long usuarioId);

    List<HistorialAccion> findByUsuarioIdInOrderByFechaDesc(List<Long> usuarioIds);

    List<HistorialAccion> findByArchivoIdOrderByFechaDesc(UUID archivoId);
}
