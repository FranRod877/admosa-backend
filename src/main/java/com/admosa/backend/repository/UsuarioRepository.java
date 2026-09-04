package com.admosa.backend.repository;

import com.admosa.backend.domain.Usuario;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmail(String email);

    List<Usuario> findByAreaId(Long areaId);

    /**
     * Trae el área ya inicializada: el resultado queda como principal de la
     * sesión durante toda la petición, y su área se lee luego en servicios
     * que corren en transacciones/sesiones de Hibernate distintas.
     */
    @Query("select u from Usuario u left join fetch u.area where u.email = :email")
    Optional<Usuario> findByEmailWithArea(String email);
}
