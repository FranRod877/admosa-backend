package com.admosa.backend.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PostPersist;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Persistable;

/**
 * El id es un UUID asignado en código (no @GeneratedValue), así que sin
 * Persistable, Spring Data JPA asume que save() de una instancia nueva es un
 * update (merge) y no un insert (persist) — implementarlo corrige eso.
 */
@Entity
@Table(name = "archivos")
@Getter
@Setter
@NoArgsConstructor
public class Archivo implements Persistable<UUID> {

    @Id
    private UUID id;

    private String nombreOriginal;

    private String storageKey;

    private String contentType;

    private long tamanio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "propietario_id")
    private Usuario propietario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "area_id")
    private Area area;

    private LocalDateTime fechaCarga;

    @Transient
    private boolean isNew = true;

    @Override
    public boolean isNew() {
        return isNew;
    }

    @PostPersist
    @PostLoad
    void markNotNew() {
        this.isNew = false;
    }
}
