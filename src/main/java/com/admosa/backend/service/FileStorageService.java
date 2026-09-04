package com.admosa.backend.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;

/**
 * Guarda los binarios en disco con un nombre generado (UUID), separado del
 * nombre original y nunca expuesto en las URLs — el cliente solo conoce el id
 * del registro Archivo en base de datos.
 */
@Service
public class FileStorageService {

    private final Path root;

    public FileStorageService(@Value("${admosa.storage.location}") String location) {
        this.root = Path.of(location).toAbsolutePath().normalize();
        try {
            Files.createDirectories(root);
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo crear el directorio de almacenamiento", e);
        }
    }

    public String store(InputStream content) {
        String storageKey = UUID.randomUUID().toString();
        Path target = root.resolve(storageKey);
        try {
            Files.copy(content, target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo guardar el archivo", e);
        }
        return storageKey;
    }

    public Resource loadAsResource(String storageKey) {
        try {
            Path filePath = root.resolve(storageKey).normalize();
            if (!filePath.startsWith(root)) {
                throw new SecurityException("Ruta de almacenamiento inválida");
            }
            return new UrlResource(filePath.toUri());
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo leer el archivo", e);
        }
    }

    public void delete(String storageKey) {
        Path filePath = root.resolve(storageKey).normalize();
        try {
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo eliminar el archivo", e);
        }
    }
}
