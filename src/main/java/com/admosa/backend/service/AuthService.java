package com.admosa.backend.service;

import com.admosa.backend.domain.Usuario;
import com.admosa.backend.dto.LoginRequest;
import com.admosa.backend.dto.LoginResponse;
import com.admosa.backend.dto.UsuarioResponse;
import com.admosa.backend.repository.UsuarioRepository;
import com.admosa.backend.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService;

    public LoginResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password()));

        Usuario usuario = usuarioRepository.findByEmail(request.email())
                .orElseThrow(() -> new IllegalStateException("Usuario autenticado no encontrado"));

        String token = jwtService.generateToken(usuario);
        return new LoginResponse(token, UsuarioResponse.from(usuario));
    }
}
