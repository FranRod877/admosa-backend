package com.admosa.backend.service;

import com.admosa.backend.domain.AccionHistorial;
import com.admosa.backend.domain.Archivo;
import com.admosa.backend.domain.HistorialAccion;
import com.admosa.backend.domain.Usuario;
import com.admosa.backend.dto.ArchivoResponse;
import com.admosa.backend.exception.ResourceNotFoundException;
import com.admosa.backend.repository.ArchivoRepository;
import com.admosa.backend.repository.HistorialAccionRepository;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class FileService {

    private final ArchivoRepository archivoRepository;
    private final HistorialAccionRepository historialAccionRepository;
    private final FileStorageService fileStorageService;
    private final FileAccessPolicy accessPolicy;

    @Transactional
    public ArchivoResponse upload(Usuario actor, MultipartFile file) {
        String storageKey;
        try {
            storageKey = fileStorageService.store(file.getInputStream());
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo leer el archivo subido", e);
        }

        Archivo archivo = new Archivo();
        archivo.setId(UUID.randomUUID());
        archivo.setNombreOriginal(file.getOriginalFilename());
        archivo.setStorageKey(storageKey);
        archivo.setContentType(file.getContentType());
        archivo.setTamanio(file.getSize());
        archivo.setPropietario(actor);
        archivo.setArea(actor.getArea());
        archivo.setFechaCarga(LocalDateTime.now());
        archivoRepository.save(archivo);

        registrarHistorial(actor, archivo, AccionHistorial.CARGA);

        return toResponse(actor, archivo);
    }

    @Transactional(readOnly = true)
    public List<ArchivoResponse> listVisiblesPara(Usuario actor) {
        List<Archivo> candidatos = switch (actor.getRol()) {
            case ADMINISTRADOR -> archivoRepository.findAllByOrderByFechaCargaDesc();
            case GERENTE -> {
                List<Long> areaIds = accessPolicy.managedAreaIds(actor);
                List<Archivo> propios = archivoRepository.findByPropietarioIdOrderByFechaCargaDesc(actor.getId());
                List<Archivo> deAreas = areaIds.isEmpty()
                        ? List.of()
                        : archivoRepository.findByAreaIdInOrderByFechaCargaDesc(areaIds);
                yield mergeSinDuplicados(propios, deAreas);
            }
            case JEFE_AREA -> {
                List<Archivo> propios = archivoRepository.findByPropietarioIdOrderByFechaCargaDesc(actor.getId());
                List<Archivo> delArea = actor.getArea() == null
                        ? List.of()
                        : archivoRepository.findByAreaIdInOrderByFechaCargaDesc(List.of(actor.getArea().getId()));
                yield mergeSinDuplicados(propios, delArea);
            }
            case USUARIO_ESTANDAR -> archivoRepository.findByPropietarioIdOrderByFechaCargaDesc(actor.getId());
        };

        return candidatos.stream()
                .filter(archivo -> accessPolicy.canView(actor, archivo))
                .map(archivo -> toResponse(actor, archivo))
                .collect(Collectors.toList());
    }

    @Transactional
    public DownloadPayload download(Usuario actor, UUID archivoId) {
        return obtenerPayload(actor, archivoId, AccionHistorial.DESCARGA);
    }

    @Transactional
    public DownloadPayload view(Usuario actor, UUID archivoId) {
        return obtenerPayload(actor, archivoId, AccionHistorial.VISUALIZACION);
    }

    private DownloadPayload obtenerPayload(Usuario actor, UUID archivoId, AccionHistorial accion) {
        Archivo archivo = obtenerArchivo(archivoId);
        if (!accessPolicy.canDownload(actor, archivo)) {
            throw new AccessDeniedException("No tienes permiso para acceder a este archivo");
        }
        Resource resource = fileStorageService.loadAsResource(archivo.getStorageKey());
        registrarHistorial(actor, archivo, accion);
        return new DownloadPayload(resource, archivo.getNombreOriginal(), archivo.getContentType());
    }

    @Transactional
    public void delete(Usuario actor, UUID archivoId) {
        Archivo archivo = obtenerArchivo(archivoId);
        if (!accessPolicy.canDelete(actor, archivo)) {
            throw new AccessDeniedException("No tienes permiso para eliminar este archivo");
        }

        // Sin referencia al archivo: está por eliminarse en esta misma transacción,
        // y Hibernate rechaza que una fila nueva referencie una que se borra en el
        // mismo flush. El nombre queda igualmente en `detalle` para el historial.
        HistorialAccion registro = new HistorialAccion();
        registro.setUsuario(actor);
        registro.setAccion(AccionHistorial.ELIMINACION);
        registro.setFecha(LocalDateTime.now());
        registro.setDetalle(archivo.getNombreOriginal());
        historialAccionRepository.save(registro);

        fileStorageService.delete(archivo.getStorageKey());
        archivoRepository.delete(archivo);
    }

    private Archivo obtenerArchivo(UUID id) {
        return archivoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Archivo no encontrado"));
    }

    private List<Archivo> mergeSinDuplicados(List<Archivo> a, List<Archivo> b) {
        return Stream.concat(a.stream(), b.stream())
                .collect(Collectors.toMap(Archivo::getId, x -> x, (x, y) -> x, LinkedHashMap::new))
                .values().stream()
                .sorted((x, y) -> y.getFechaCarga().compareTo(x.getFechaCarga()))
                .collect(Collectors.toList());
    }

    private void registrarHistorial(Usuario actor, Archivo archivo, AccionHistorial accion) {
        HistorialAccion registro = new HistorialAccion();
        registro.setUsuario(actor);
        registro.setArchivo(archivo);
        registro.setAccion(accion);
        registro.setFecha(LocalDateTime.now());
        registro.setDetalle(archivo.getNombreOriginal());
        historialAccionRepository.save(registro);
    }

    private ArchivoResponse toResponse(Usuario actor, Archivo archivo) {
        return new ArchivoResponse(
                archivo.getId(),
                archivo.getNombreOriginal(),
                archivo.getContentType(),
                archivo.getTamanio(),
                archivo.getPropietario().getId(),
                archivo.getPropietario().getNombre(),
                archivo.getArea() != null ? archivo.getArea().getNombre() : null,
                archivo.getFechaCarga(),
                accessPolicy.canDownload(actor, archivo),
                accessPolicy.canDelete(actor, archivo));
    }

    public record DownloadPayload(Resource resource, String filename, String contentType) {
    }
}
