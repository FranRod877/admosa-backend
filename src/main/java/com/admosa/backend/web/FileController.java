package com.admosa.backend.web;

import com.admosa.backend.dto.ArchivoResponse;
import com.admosa.backend.security.CustomUserDetails;
import com.admosa.backend.service.FileService;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ArchivoResponse upload(
            @AuthenticationPrincipal CustomUserDetails principal,
            @RequestParam("file") MultipartFile file) {
        return fileService.upload(principal.getUsuario(), file);
    }

    @GetMapping
    public List<ArchivoResponse> list(@AuthenticationPrincipal CustomUserDetails principal) {
        return fileService.listVisiblesPara(principal.getUsuario());
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> download(
            @AuthenticationPrincipal CustomUserDetails principal,
            @PathVariable UUID id) {
        FileService.DownloadPayload payload = fileService.download(principal.getUsuario(), id);
        MediaType mediaType = payload.contentType() != null
                ? MediaType.parseMediaType(payload.contentType())
                : MediaType.APPLICATION_OCTET_STREAM;

        return ResponseEntity.ok()
                .contentType(mediaType)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + payload.filename() + "\"")
                .body(payload.resource());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@AuthenticationPrincipal CustomUserDetails principal, @PathVariable UUID id) {
        fileService.delete(principal.getUsuario(), id);
    }
}
