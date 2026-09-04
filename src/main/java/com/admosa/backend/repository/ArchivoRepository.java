package com.admosa.backend.repository;

import com.admosa.backend.domain.Archivo;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArchivoRepository extends JpaRepository<Archivo, UUID> {

    List<Archivo> findAllByOrderByFechaCargaDesc();

    List<Archivo> findByPropietarioIdOrderByFechaCargaDesc(Long propietarioId);

    List<Archivo> findByAreaIdInOrderByFechaCargaDesc(List<Long> areaIds);
}
