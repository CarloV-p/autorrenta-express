-- Esquema AutoRenta Express (SQLite). No usar ';' dentro de textos.

CREATE TABLE IF NOT EXISTS usuario (
    id_usuario     INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre_usuario TEXT NOT NULL UNIQUE,
    password_hash  TEXT NOT NULL,
    rol            TEXT NOT NULL CHECK (rol IN ('ADMINISTRADOR','AGENTE')),
    activo         INTEGER NOT NULL DEFAULT 1
);

CREATE TABLE IF NOT EXISTS cliente (
    id_cliente INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre     TEXT NOT NULL,
    dni        TEXT NOT NULL UNIQUE,
    telefono   TEXT
);

CREATE TABLE IF NOT EXISTS conductor (
    id_conductor                INTEGER PRIMARY KEY AUTOINCREMENT,
    dni                         TEXT NOT NULL UNIQUE,
    nombre                      TEXT NOT NULL,
    nro_licencia                TEXT NOT NULL UNIQUE,
    fecha_vencimiento_licencia  TEXT
);

CREATE TABLE IF NOT EXISTS vehiculo (
    placa                      TEXT PRIMARY KEY,
    marca                      TEXT NOT NULL,
    modelo                     TEXT NOT NULL,
    tipo                       TEXT NOT NULL CHECK (tipo IN ('ECONOMICO','SUV_4X4','CAMIONETA_CARGA','ELECTRICO')),
    estado                     TEXT NOT NULL CHECK (estado IN ('DISPONIBLE','ALQUILADO','EN_MANTENIMIENTO')),
    kilometraje                REAL NOT NULL DEFAULT 0,
    precio_base_por_dia        REAL NOT NULL,
    km_proximo_mantenimiento   REAL NOT NULL
);

CREATE TABLE IF NOT EXISTS cobertura (
    id_cobertura INTEGER PRIMARY KEY AUTOINCREMENT,
    tipo         TEXT NOT NULL UNIQUE,
    nombre       TEXT NOT NULL,
    costo_por_dia REAL NOT NULL
);

CREATE TABLE IF NOT EXISTS contrato (
    id_contrato        INTEGER PRIMARY KEY AUTOINCREMENT,
    id_cliente         INTEGER NOT NULL REFERENCES cliente(id_cliente),
    placa              TEXT NOT NULL REFERENCES vehiculo(placa),
    fecha_inicio       TEXT NOT NULL,
    fecha_fin          TEXT NOT NULL,
    tipo_contrato      TEXT NOT NULL CHECK (tipo_contrato IN ('SIMPLE','CORPORATIVO')),
    tipo_cobro         TEXT NOT NULL,
    estado_contrato    TEXT NOT NULL CHECK (estado_contrato IN ('ACTIVO','FINALIZADO','CANCELADO')),
    monto_total        REAL NOT NULL DEFAULT 0,
    deposito_garantia  REAL NOT NULL DEFAULT 0,
    km_pactado         REAL NOT NULL DEFAULT 0,
    km_inicial         REAL NOT NULL DEFAULT 0,
    km_final           REAL,
    razon_social       TEXT,
    ruc                TEXT,
    limite_km_mensual  REAL
);

CREATE TABLE IF NOT EXISTS contrato_conductor (
    id_contrato  INTEGER NOT NULL REFERENCES contrato(id_contrato) ON DELETE CASCADE,
    id_conductor INTEGER NOT NULL REFERENCES conductor(id_conductor),
    PRIMARY KEY (id_contrato, id_conductor)
);

CREATE TABLE IF NOT EXISTS contrato_cobertura (
    id_contrato  INTEGER NOT NULL REFERENCES contrato(id_contrato) ON DELETE CASCADE,
    id_cobertura INTEGER NOT NULL REFERENCES cobertura(id_cobertura),
    PRIMARY KEY (id_contrato, id_cobertura)
);

CREATE TABLE IF NOT EXISTS tramo_contrato (
    id_tramo     INTEGER PRIMARY KEY AUTOINCREMENT,
    id_contrato  INTEGER NOT NULL REFERENCES contrato(id_contrato) ON DELETE CASCADE,
    fecha_inicio TEXT NOT NULL,
    fecha_fin    TEXT NOT NULL,
    destino      TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS mantenimiento (
    id_mantenimiento INTEGER PRIMARY KEY AUTOINCREMENT,
    placa            TEXT NOT NULL REFERENCES vehiculo(placa),
    fecha            TEXT NOT NULL,
    tipo             TEXT NOT NULL,
    estado           TEXT NOT NULL,
    costo            REAL NOT NULL DEFAULT 0,
    descripcion      TEXT
);

CREATE TABLE IF NOT EXISTS pago (
    id_pago     INTEGER PRIMARY KEY AUTOINCREMENT,
    id_contrato INTEGER NOT NULL REFERENCES contrato(id_contrato),
    fecha       TEXT NOT NULL,
    monto       REAL NOT NULL,
    metodo      TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS alerta_mantenimiento (
    id_alerta          INTEGER PRIMARY KEY AUTOINCREMENT,
    placa              TEXT NOT NULL REFERENCES vehiculo(placa),
    km_actual          REAL NOT NULL,
    km_limite          REAL NOT NULL,
    fecha_generacion   TEXT NOT NULL,
    mensaje            TEXT NOT NULL,
    atendida           INTEGER NOT NULL DEFAULT 0
);

-- ===== Datos iniciales (idempotentes) =====
INSERT OR IGNORE INTO usuario (nombre_usuario, password_hash, rol) VALUES ('admin', '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', 'ADMINISTRADOR');
INSERT OR IGNORE INTO usuario (nombre_usuario, password_hash, rol) VALUES ('agente', 'ddabaa1ea73966696802178a1a075effa58c73b71c1a44a5de551f90ff76b988', 'AGENTE');

INSERT OR IGNORE INTO cobertura (tipo, nombre, costo_por_dia) VALUES ('TODO_RIESGO', 'Seguro Todo Riesgo', 25.0);
INSERT OR IGNORE INTO cobertura (tipo, nombre, costo_por_dia) VALUES ('GPS', 'Dispositivo GPS', 8.0);
INSERT OR IGNORE INTO cobertura (tipo, nombre, costo_por_dia) VALUES ('SILLA_BEBE', 'Silla de Bebé', 5.0);
INSERT OR IGNORE INTO cobertura (tipo, nombre, costo_por_dia) VALUES ('CONDUCTOR_ADICIONAL', 'Conductor Adicional', 10.0);

INSERT OR IGNORE INTO vehiculo VALUES ('ABC-123', 'Toyota', 'Yaris', 'ECONOMICO', 'DISPONIBLE', 12000, 80.0, 20000);
INSERT OR IGNORE INTO vehiculo VALUES ('DEF-456', 'Toyota', 'Hilux 4x4', 'SUV_4X4', 'DISPONIBLE', 30000, 180.0, 40000);
INSERT OR IGNORE INTO vehiculo VALUES ('GHI-789', 'Hyundai', 'H100', 'CAMIONETA_CARGA', 'DISPONIBLE', 45000, 150.0, 50000);
INSERT OR IGNORE INTO vehiculo VALUES ('JKL-012', 'BYD', 'Dolphin', 'ELECTRICO', 'DISPONIBLE', 5000, 140.0, 15000);

INSERT OR IGNORE INTO cliente (nombre, dni, telefono) VALUES ('Cliente Demo', '12345678', '987654321');
INSERT OR IGNORE INTO conductor (dni, nombre, nro_licencia, fecha_vencimiento_licencia) VALUES ('87654321', 'Conductor Demo', 'Q12345678', '2030-12-31');
