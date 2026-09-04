package com.admosa.backend.service;

import com.admosa.backend.dto.AreaResponse;
import com.admosa.backend.repository.AreaRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AreaService {

    private final AreaRepository areaRepository;

    @Transactional(readOnly = true)
    public List<AreaResponse> listAll() {
        return areaRepository.findAll().stream()
                .map(AreaResponse::from)
                .collect(Collectors.toList());
    }
}
