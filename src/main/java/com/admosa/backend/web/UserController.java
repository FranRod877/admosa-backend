package com.admosa.backend.web;

import com.admosa.backend.dto.UpdateUsuarioRequest;
import com.admosa.backend.dto.UsuarioResponse;
import com.admosa.backend.service.UserService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMINISTRADOR')")
public class UserController {

    private final UserService userService;

    @GetMapping
    public List<UsuarioResponse> listAll() {
        return userService.listAll();
    }

    @PatchMapping("/{id}")
    public UsuarioResponse update(@PathVariable Long id, @RequestBody UpdateUsuarioRequest request) {
        return userService.update(id, request);
    }
}
