CREATE TABLE areas (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL UNIQUE,
    gerente_id BIGINT NULL
);

CREATE TABLE usuarios (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(150) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    rol VARCHAR(30) NOT NULL,
    area_id BIGINT NULL REFERENCES areas (id)
);

ALTER TABLE areas
    ADD CONSTRAINT fk_areas_gerente FOREIGN KEY (gerente_id) REFERENCES usuarios (id);

CREATE TABLE archivos (
    id UUID PRIMARY KEY,
    nombre_original VARCHAR(255) NOT NULL,
    storage_key VARCHAR(100) NOT NULL UNIQUE,
    content_type VARCHAR(150),
    tamanio BIGINT NOT NULL,
    propietario_id BIGINT NOT NULL REFERENCES usuarios (id),
    area_id BIGINT NULL REFERENCES areas (id),
    fecha_carga TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE historial_acciones (
    id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT NOT NULL REFERENCES usuarios (id),
    archivo_id UUID NULL REFERENCES archivos (id) ON DELETE SET NULL,
    accion VARCHAR(30) NOT NULL,
    fecha TIMESTAMP NOT NULL DEFAULT now(),
    detalle VARCHAR(255)
);

CREATE INDEX idx_archivos_propietario ON archivos (propietario_id);
CREATE INDEX idx_archivos_area ON archivos (area_id);
CREATE INDEX idx_historial_usuario ON historial_acciones (usuario_id);
CREATE INDEX idx_historial_archivo ON historial_acciones (archivo_id);
