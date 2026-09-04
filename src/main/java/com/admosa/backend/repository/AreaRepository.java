package com.admosa.backend.repository;

import com.admosa.backend.domain.Area;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AreaRepository extends JpaRepository<Area, Long> {

    List<Area> findByGerenteId(Long gerenteId);
}
