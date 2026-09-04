package com.admosa.backend.seed;

import com.admosa.backend.domain.Area;
import com.admosa.backend.domain.Rol;
import com.admosa.backend.domain.Usuario;
import com.admosa.backend.repository.AreaRepository;
import com.admosa.backend.repository.UsuarioRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Crea los usuarios de prueba mínimos requeridos (uno por rol) al arrancar
 * en una base de datos vacía, con contraseñas hasheadas con el encoder real
 * de la app en lugar de hashes precalculados en SQL.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class DataSeeder implements ApplicationRunner {

    private static final String DEFAULT_PASSWORD = "Password123!";

    private final UsuarioRepository usuarioRepository;
    private final AreaRepository areaRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (usuarioRepository.count() > 0) {
            return;
        }

        Area ventas = areaRepository.save(new Area(null, "Ventas", null));
        Area tecnologia = areaRepository.save(new Area(null, "Tecnología", null));

        Usuario admin = crearUsuario("Ana Administradora", "admin@admosa.com", Rol.ADMINISTRADOR, null);
        Usuario gerente = crearUsuario("Gerardo Gerente", "gerente@admosa.com", Rol.GERENTE, null);
        Usuario jefeArea = crearUsuario("Jaime Jefe", "jefe@admosa.com", Rol.JEFE_AREA, ventas);
        Usuario usuarioVentas = crearUsuario("Uriel Usuario", "usuario@admosa.com", Rol.USUARIO_ESTANDAR, ventas);
        Usuario usuarioTecnologia =
                crearUsuario("Tania Técnica", "usuario2@admosa.com", Rol.USUARIO_ESTANDAR, tecnologia);

        usuarioRepository.saveAll(List.of(admin, gerente, jefeArea, usuarioVentas, usuarioTecnologia));

        ventas.setGerente(gerente);
        tecnologia.setGerente(gerente);
        areaRepository.saveAll(List.of(ventas, tecnologia));

        log.info("Usuarios de prueba creados (password para todos: '{}'):", DEFAULT_PASSWORD);
        log.info(" - admin@admosa.com        ADMINISTRADOR");
        log.info(" - gerente@admosa.com      GERENTE (gestiona Ventas y Tecnología)");
        log.info(" - jefe@admosa.com         JEFE_AREA (Ventas)");
        log.info(" - usuario@admosa.com      USUARIO_ESTANDAR (Ventas)");
        log.info(" - usuario2@admosa.com     USUARIO_ESTANDAR (Tecnología)");
    }

    private Usuario crearUsuario(String nombre, String email, Rol rol, Area area) {
        Usuario usuario = new Usuario();
        usuario.setNombre(nombre);
        usuario.setEmail(email);
        usuario.setPasswordHash(passwordEncoder.encode(DEFAULT_PASSWORD));
        usuario.setRol(rol);
        usuario.setArea(area);
        return usuario;
    }
}
