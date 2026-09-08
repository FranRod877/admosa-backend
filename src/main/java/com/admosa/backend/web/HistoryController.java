package com.admosa.backend.web;

import com.admosa.backend.dto.HistorialResponse;
import com.admosa.backend.security.CustomUserDetails;
import com.admosa.backend.service.HistoryService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/history")
@RequiredArgsConstructor
public class HistoryController {

    private final HistoryService historyService;

    @GetMapping
    @PreAuthorize("hasAnyRole('GERENTE', 'ADMINISTRADOR')")
    public List<HistorialResponse> historial(@AuthenticationPrincipal CustomUserDetails principal) {
        return historyService.historialPara(principal.getUsuario());
    }
}
