# BlackJackAPI

API REST para jugar al Blackjack de forma reactiva con Spring Boot.

## Características

- Juego completo de Blackjack (hit, stand, double, split, insurance)
- Sistema de autenticación JWT
- Sistema de Ranking de jugadores
- Programación reactiva con Spring WebFlux
- Base de datos MySQL con R2DBC

## Requisitos

- Java 21
- Maven 3.9+
- Docker (opcional, para MySQL)

## Instalación

### Prerrequisito: MySQL

La API requiere una base de datos MySQL en ejecución. Hay dos opciones:

#### Opción A: Docker (Recomendado)

```bash
# Iniciar MySQL (debe estar corriendo ANTES de ejecutar la app)
docker-compose up -d

# Verificar que MySQL está listo
docker logs blackjack_mysql  # debe dir "ready for connections"
```

#### Opción B: MySQL local

```bash
# Instalar MySQL y crear la base de datos
mysql -u root -p < src/main/resources/schema.sql
```

### Compilar y ejecutar

```bash
./mvnw clean install
./mvnw spring-boot:run
```

### Detener MySQL

```bash
docker-compose down
```

> **Nota**: Sin MySQL funcionando, la app arrancará pero fallará al intentar acceder a la base de datos.

## Notas de Configuración

### Versión de Spring Boot

**Importante**: Este proyecto usa **Spring Boot 3.3.5** (no 3.4.x) debido a problemas de compatibilidad entre r2dbc-mysql y Netty en Spring Boot 3.4.

### Solución al problema de R2DBC

El problema de mapeo de columnas con R2DBC se resolvió:
1. Renombrando las columnas `rank` a `card_rank` (evitando palabra reservada)
2. Usando queries nativas en los repositories
3. Convertiendo manualmente los valores de String a Enum

### Estado de Funcionalidades

| Operación | Estado |
|-----------|--------|
| Registro de usuario | ✅ Funciona |
| Login | ✅ Funciona |
| Crear partida | ✅ Funciona |
| Apostar (BET) | ✅ Funciona |
| HIT/STAND | ✅ Funciona |
| Ver partida | ✅ Funciona |
| Ranking | ✅ Funciona |
| Tests unitarios | ✅ 25 tests pasan |

## Configuración

Editar `src/main/resources/application.properties`:

```properties
spring.r2dbc.url=r2dbc:mysql://localhost:3306/blackjack
spring.r2dbc.username=root
spring.r2dbc.password=password

jwt.secret=tu-secreto-jwt
jwt.expiration=86400000
```

## API Endpoints

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | /auth/register | Registrar jugador |
| POST | /auth/login | Iniciar sesión |
| POST | /game/new | Crear partida |
| GET | /game/{id} | Ver partida |
| POST | /game/{id}/play | Jugar acción |
| DELETE | /game/{id}/delete | Eliminar partida |
| PUT | /player/{id} | Actualizar perfil |
| GET | /ranking | Ver ranking |

## Tecnologías

- Spring Boot 3.4.0
- Spring WebFlux
- Spring Security
- R2DBC + MySQL
- JWT (jjwt)

## Licencia

Copyright 2026 Ulises Lafuente. Licensed under Apache License 2.0.

Ver LICENSE para más detalles.