# BlackJackAPI - Resumen del Programa

## Descripción General

BlackJackAPI es una API REST desarrollada con Spring Boot (WebFlux) para jugar al Blackjack de forma reactiva. Permite a los jugadores registrarse, autenticarse, crear partidas, jugar (hit, stand, double, split, insurance) y consultar rankings.

## Arquitectura

### Stack Tecnológico
- **Framework**: Spring Boot 3.3.5 (usado por compatibilidad con R2DBC)
- **Programación Reactiva**: Spring WebFlux ( Reactor )
- **Base de Datos**: MySQL con R2DBC (reactiva)
- **Seguridad**: Spring Security + JWT
- **Java**: Versión 21

### Patrones de Diseño
- **DTO (Data Transfer Object)**: Para transferencia de datos entre capas
- **Repository Pattern**: Acceso a datos reactivo
- **Service Layer**: Lógica de negocio
- **REST Controller**: Endpoints de la API

## Dependencias Principales

| Dependencia | Función |
|-------------|---------|
| spring-boot-starter-webflux | API REST reactiva |
| spring-boot-starter-data-r2dbc | Acceso reactivo a MySQL |
| spring-boot-starter-security | Autenticación y autorización |
| spring-boot-starter-validation | Validación de entradas |
| r2dbc-mysql | Driver MySQL reactivo |
| jjwt | Generación y validación de JWT |
| lombok | Reducción de boilerplate |

## Estructura del Proyecto

```
src/main/java/com/Ulises/BlackJackAPI/
├── BlackJackApiApplication.java        # Punto de entrada
├── controller/                         # Controladores REST
│   ├── AuthController.java             # Registro y login
│   ├── GameController.java             # Gestión de partidas
│   ├── PlayerController.java           # Perfil de jugador
│   └── RankingController.java         # Clasificación
├── service/                            # Lógica de negocio
│   ├── AuthService.java                # Autenticación
│   ├── GameService.java                # Lógica del juego
│   ├── PlayerService.java              # Gestión de jugadores
│   └── DeckService.java                # Manejo de la baraja
├── security/                           # Seguridad JWT
│   ├── JwtUtil.java                    # Generación/validación de tokens
│   ├── JwtAuthenticationFilter.java   # Filtro de autenticación
│   ├── PlayerUserDetails.java          # Usuario personalizado
│   └── PlayerUserDetailsService.java  # Carga de usuarios
├── config/                             # Configuración
│   └── SecurityConfig.java             # Reglas de seguridad
├── dto/                                # Objetos de transferencia
│   ├── ApiResponse.java                # Respuesta genérica
│   ├── AuthResponse.java               # Token y datos de usuario
│   ├── GameResponse.java               # Estado del juego
│   ├── PlayRequest.java                # Acción del jugador
│   └── ...
├── exception/                          # Manejo de errores
│   ├── GlobalExceptionHandler.java     # Manejador centralizado
│   ├── GameNotFoundException.java
│   ├── PlayerNotFoundException.java
│   └── InvalidMoveException.java
├── repository/                         # Repositorios R2DBC
│   ├── PlayerRepository.java
│   ├── GameRepository.java
│   ├── HandRepository.java
│   ├── CardRepository.java
│   └── DeckRepository.java
└── domain/                             # Dominio (DDD)
    ├── enums/                          # Enumeraciones
    │   ├── GameStatus.java             # Estado: BETTING, PLAYER_TURN, CROUPIER_TURN, FINISHED
    │   ├── GameResult.java             # Resultado: WIN, LOSE, PUSH, BLACKJACK, etc.
    │   ├── HandType.java               # Tipo: PLAYER, CROUPIER
    │   ├── Rank.java                   # Rango: ACE, TWO...KING
    │   └── Suit.java                   # Palo: HEARTS, DIAMONDS, CLUBS, SPADES
    ├── entity/                         # Entidades de BD
    │   ├── PlayerEntity.java
    │   ├── GameEntity.java
    │   ├── HandEntity.java
    │   ├── CardEntity.java
    │   └── DeckEntity.java
    ├── factory/
    │   └── CardFactory.java            # Factoría de entidades
    └── services/                       # Servicios de dominio
        ├── GameRulesEngine.java        # Motor de reglas del juego
        └── ScoreCalculator.java        # Calculadora de puntuación
```

> **Nota**: La estructura sigue el patrón DDD (Domain-Driven Design), donde `domain/` contiene todos los elementos del dominio del negocio (enums, entidades, value objects, y servicios).

## Endpoints de la API

### Autenticación
- `POST /auth/register` - Registrar nuevo jugador
- `POST /auth/login` - Iniciar sesión

### Juego
- `POST /game/new` - Crear nueva partida
- `GET /game/{id}` - Ver estado de una partida
- `POST /game/{id}/play` - Realizar acción (BET, HIT, STAND, DOUBLE, SPLIT, INSURANCE)
- `DELETE /game/{id}/delete` - Eliminar partida

### Jugador
- `PUT /player/{playerId}` - Actualizar nombre de usuario

### Rankings
- `GET /ranking` - Ver clasificación de jugadores

## Flujo del Juego

1. **Crear partida**: El jugador crea una nueva partida (estado: BETTING)
2. **Apostar**: El jugador envía BET con el monto (estado: PLAYER_TURN)
3. **Repartir cartas**: Se reparten 2 al jugador y 2 al crupier (una oculta)
4. **Turno del jugador**: Puede hacer HIT, STAND, DOUBLE, SPLIT, o INSURANCE
5. **Turno del crupier**: El crupier revela su carta y juega según reglas
6. **Resolver**: Se determina el resultado y se actualizan las puntuaciones

## Seguridad

- Autenticación mediante JWT (token en header Authorization)
- Contraseñas cifradas con BCrypt
- Endpoints `/auth/**` públicos, resto requieren autenticación

## Validación

El programa pasó 86 tests unitarios cubriendo:
- Lógica de puntuación de cartas
- Reglas del juego
- Generación y validación de JWT
- Respuestas API

## Historial de Cambios

- **2026-05-07**: Refactorización DDD y corrección de R2DBC
  - Movido `model/enums`, `model/entity`, `model/valueobject` a `domain/`
  - Eliminadas clases redundantes (`domain/game/`)
  - Renombrado `domain/service` a `domain/services`
  - Solucionado problema de R2DBC con MySQL: renombrada columna `rank` a `card_rank`
  - Añadidas queries nativas en repositories para evitar problemas de mapeo
  - Todas las operaciones de juego funcionando (BET, HIT, STAND, etc.)

## Autor

Ulises Lafuente