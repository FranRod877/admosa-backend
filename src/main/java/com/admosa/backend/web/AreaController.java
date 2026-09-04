package com.admosa.backend.web;

import com.admosa.backend.dto.AreaResponse;
import com.admosa.backend.service.AreaService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/areas")
@RequiredArgsConstructor
public class AreaController {

    private final AreaService areaService;

    @GetMapping
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public List<AreaResponse> listAll() {
        return areaService.listAll();
    }
}
