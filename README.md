# BlackJackAPI

API REST para jugar al Blackjack de forma reactiva con Spring Boot.

## Características

- Juego completo de Blackjack (hit, stand, double, split, insurance)
- Sistema de autenticación JWT seguro
- Sistema de Ranking de jugadores
- Programación reactiva con Spring WebFlux
- Base de datos MySQL con R2DBC
- Rate limiting para protección de ataques
- Validación de entrada robusta
- Dockerizado para fácil despliegue

## Tecnologías

- Spring Boot 3.3.5
- Spring WebFlux (Reactivo)
- Spring Security
- R2DBC + MySQL
- JWT (jjwt 0.12.5)
- Lombok
- JUnit 5 + Mockito
- JaCoCo (Cobertura de tests)

## Requisitos

- Java 21
- Maven 3.9+
- Docker y Docker Compose (opcional)

## Instalación

### Prerrequisito: MySQL

La API requiere una base de datos MySQL en ejecución. Hay dos opciones:

#### Opción A: Docker (Recomendado)

```bash
# Iniciar MySQL
docker-compose up -d

# Verificar que MySQL está listo
docker logs blackjack_mysql  # debe dir "ready for connections"
```

#### Opción B: MySQL local

```bash
# Crear la base de datos
mysql -u root -p < src/main/resources/schema.sql
```

### Compilar y ejecutar

```bash
# Compilar
./mvnw clean package

# Ejecutar
java -jar target/BlackJackAPI-0.0.1-SNAPSHOT.jar
```

## Dockerización

### Construir imagen Docker

```bash
docker build -t blackjackapi:latest .
```

### Ejecutar con Docker Compose

```bash
# Iniciar todos los servicios (MySQL + API)
docker-compose up -d

# Ver estado
docker-compose ps

# Ver logs
docker-compose logs -f
```

### Imagen pre-construida

La imagen también está disponible en Docker Hub:

```bash
# Descargar imagen
docker pull uliseslafuentebootcamp/blackjackapi:latest

# Ejecutar
docker run -p 8080:8080 -e DB_URL=r2dbc:mysql://host:3306/blackjack \
           -e DB_PASSWORD=root uliseslafuentebootcamp/blackjackapi:latest
```

## Configuración

### Variables de Entorno

| Variable | Default | Descripción |
|----------|---------|-------------|
| DB_URL | r2dbc:mysql://localhost:3306/blackjack | URL de la base de datos |
| DB_USERNAME | root | Usuario de MySQL |
| DB_PASSWORD | root | Password de MySQL |
| JWT_SECRET | ... | Clave secreta para JWT |
| JWT_EXPIRATION | 86400000 | Expiración del token (ms) |
| SERVER_PORT | 8080 | Puerto del servidor |

### application.properties

```properties
spring.r2dbc.url=${DB_URL:r2dbc:mysql://localhost:3306/blackjack}
spring.r2dbc.username=${DB_USERNAME:root}
spring.r2dbc.password=${DB_PASSWORD:root}

jwt.secret=${JWT_SECRET:...}
jwt.expiration=${JWT_EXPIRATION:86400000}
```

## API Endpoints

### Autenticación

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | /auth/register | Registrar nuevo jugador |
| POST | /auth/login | Iniciar sesión |

### Juego

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | /game/new | Crear nueva partida |
| GET | /game/{id} | Ver estado de partida |
| POST | /game/{id}/play | Ejecutar acción (BET, HIT, STAND, DOUBLE, SPLIT, INSURANCE) |
| DELETE | /game/{id}/delete | Abandonar/Cancelar partida |

### Jugador

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| PUT | /player/{id} | Actualizar username |
| GET | /ranking | Ver ranking de jugadores |

### Acciones del Juego

```json
// BET - Apostar
{"action": "BET", "bet": 100}

// HIT - Pedir carta
{"action": "HIT"}

// STAND - Plantarse
{"action": "STAND"}

// DOUBLE - Doblar apuesta
{"action": "DOUBLE"}

// SPLIT - Dividir manos (si tiene dos cartas del mismo valor)
{"action": "SPLIT"}

// INSURANCE - Comprar seguro
{"action": "INSURANCE"}
```

## Tests y Cobertura

### Ejecutar Tests

```bash
./mvnw test
```

### Cobertura de Código

```bash
./mvnw jacoco:report
# Ver reporte en target/site/jacoco/index.html
```

### Estado de Tests

| Suite | Tests | Estado |
|-------|-------|--------|
| Unitarios | 108 | ✅ Todos pasando |
| Cobertura instrucciones | 43% | ⚠️ En mejora |
| Cobertura ramas | 30% | ⚠️ En mejora |

## Mejoras Implementadas

### Seguridad
- ✅ Validación de entrada con Jakarta Validation
- ✅ Mensajes de error genéricos (previene enumeración de usuarios)
- ✅ Rate limiting (60 req/min por cliente)
- ✅ Contraseñas hasheadas con BCrypt

### Rendimiento
- ✅ Índices en base de datos
- ✅ Optimización N+1 queries
- ✅ Queries optimizadas con JOIN

### Código
- ✅ Eliminación de código duplicado
- ✅ Exceptions personalizadas
- ✅ Configuración con variables de entorno
- ✅ HandService separado del GameService

## Estado de Funcionalidades

| Operación | Estado |
|-----------|--------|
| Registro de usuario | ✅ |
| Login con JWT | ✅ |
| Crear partida | ✅ |
| Apostar (BET) | ✅ |
| HIT - Pedir carta | ✅ |
| STAND - Plantarse | ✅ |
| DOUBLE - Doblar | ✅ |
| SPLIT - Dividir | ✅ |
| INSURANCE - Seguro | ✅ |
| Blackjack natural | ✅ |
| Ver ranking | ✅ |
| Actualizar perfil | ✅ |
| Rate limiting | ✅ |
| Docker | ✅ |

## Desarrollo

### Estructura del Proyecto

```
src/
├── main/
│   ├── java/com/Ulises/BlackJackAPI/
│   │   ├── config/          # Configuración
│   │   ├── controller/     # Controladores REST
│   │   ├── domain/         # Entidades y lógica de dominio
│   │   │   ├── entity/     # Entidades DB
│   │   │   ├── enums/      # Enums
│   │   │   ├── factory/    # Factory pattern
│   │   │   └── services/  # Servicios de dominio
│   │   ├── dto/            # Data Transfer Objects
│   │   ├── exception/     # Excepciones personalizadas
│   │   ├── repository/     # Repositorios R2DBC
│   │   ├── security/       # Seguridad JWT
│   │   └── service/        # Servicios de negocio
│   └── resources/
│       ├── application.properties
│       └── schema.sql
└── test/                   # Tests unitarios
```

### Compilar sin Tests

```bash
./mvnw clean package -DskipTests
```

## Licencia

Copyright 2026 Ulises Lafuente. Licensed under Apache License 2.0.

Ver LICENSE para más detalles.