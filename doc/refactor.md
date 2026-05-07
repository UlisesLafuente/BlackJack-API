# Refactorización a DDD Hexagonal con Modelos Ricos

## Estado Actual (Arquitectura Monolítica)

La aplicación actual tiene una estructura plana por capas:
- `controller/` - Controllers REST
- `service/` - Lógica de negocio mezclada con acceso a datos
- `repository/` - Repositorios R2DBC
- `model/entity/` - Entities anémicas (solo getters/setters)
- `model/valueobject/` - Value Objects simples
- `dto/` - Objetos de transferencia
- `security/` - Configuración de seguridad

**Problemas identificados:**
1. Services containen lógica de negocio + acceso a datos + transformación DTOs
2. Entities son anémicas (solo datos, sin comportamiento)
3. Repositorios exponen entidades de base de datos directamente
4. Ausencia de límites de contexto bounded
5. Lógica de dominio dispersa en los servicios

---

## Arquitectura Destino: DDD Hexagonal

### Estructura de Paquetes Propuesta

```
src/main/java/com/Ulises/BlackJackAPI/
├── domain/                          # Core del dominio (sin dependencias externas)
│   ├── model/                      # Modelos ricos del dominio
│   │   ├── player/                # Aggregate Player
│   │   │   ├── Player.java        # Aggregate Root
│   │   │   └── events/            # Domain Events
│   │   ├── game/                  # Aggregate Game
│   │   │   ├── Game.java          # Aggregate Root
│   │   │   ├── Hand.java          # Entity
│   │   │   └── Card.java          # Value Object (migrado desde valueobject)
│   │   ├── deck/                  # Aggregate Deck
│   │   │   ├── Deck.java
│   │   │   └── DeckFactory.java   # Factory para crear mazos
│   │   └── shared/                # Value Objects globales
│   │       ├── Suit.java
│   │       ├── Rank.java
│   │       └── Money.java         # Value Object para dinero
│   ├── services/                   # Domain Services (lógica de dominio pura)
│   │   ├── BlackjackScoringService.java
│   │   ├── GameRulesService.java
│   │   └── BettingService.java
│   ├── ports/                     # Puertos (interfaces)
│   │   ├── inbound/               # Puertos de entrada (Use Cases)
│   │   │   ├── CreateGameUseCase.java
│   │   │   ├── PlaceBetUseCase.java
│   │   │   ├── PlayerActionUseCase.java
│   │   │   └── RankingUseCase.java
│   │   └── outbound/              # Puertos de salida (repositories)
│   │       ├── PlayerRepositoryPort.java
│   │       ├── GameRepositoryPort.java
│   │       └── DeckRepositoryPort.java
│   └── exceptions/                # Excepciones de dominio
│       ├── InsufficientFundsException.java
│       ├── InvalidBetException.java
│       └── GameRuleViolationException.java
│
├── application/                    # Capa de aplicación (orquesta Use Cases)
│   ├── services/                  # Application Services
│   │   ├── GameApplicationService.java
│   │   ├── AuthApplicationService.java
│   │   └── RankingApplicationService.java
│   ├── dto/                      # DTOs de aplicación (diferentes de infrastructure)
│   │   └── mappers/              # Mappers entre dominio y DTO
│   └── config/                   # Configuración de aplicación
│
├── infrastructure/                # Adaptadores (implementación de puertos)
│   ├── persistence/              # Implementación de repositories
│   │   ├── repositories/
│   │   │   ├── PlayerRepositoryAdapter.java
│   │   │   ├── GameRepositoryAdapter.java
│   │   │   └── DeckRepositoryAdapter.java
│   │   ├── entities/            # Entities de infraestructura (ORM)
│   │   └── mappers/             # Mappers entre infraestructura y dominio
│   ├── api/                     # Controllers (adaptadores de entrada)
│   │   ├── dto/                 # DTOs de API
│   │   └── controllers/
│   │       ├── AuthController.java
│   │       ├── GameController.java
│   │       └── RankingController.java
│   ├── security/                # Configuración de seguridad
│   │   ├── JwtUtil.java
│   │   ├── JwtAuthenticationFilter.java
│   │   └── SecurityConfig.java
│   └── config/                  # Configuración de infraestructura
│
└── BlackJackAPIApplication.java  # Punto de entrada
```

---

## Cambios Necesarios: Punto por Punto

### 1. Crear modelo rico Player (Aggregate Root)

**Cambio:** Migrar `PlayerEntity` → `domain/model/player/Player.java`

```java
// ANTES: PlayerEntity (anémica)
public class PlayerEntity {
    private Long id;
    private String username;
    private Integer score; // simple int
    // getters/setters
}

// DESPUÉS: Player (modelo rico)
public class Player {
    private PlayerId id;
    private Username username;
    private Money balance;          // Value Object con validación

    public void addWinnings(Money amount) { ... }
    public void deductBet(Money amount) { ... }
    public boolean canAfford(Money amount) { ... }
    // Métodos de dominio
}
```

**Value Objects a crear:**
- `PlayerId` (wrapper de Long con validación)
- `Username` (validación de negocio)
- `Money` (operaciones aritméticas con validación de negativos)

---

### 2. Crear modelo rico Game (Aggregate Root)

**Cambio:** Migrar `GameEntity` + lógica de `GameService` → `domain/model/game/`

```java
// Game.java - Aggregate Root
public class Game {
    private GameId id;
    private PlayerId playerId;
    private GameStatus status;
    private List<Hand> hands;       // Entity manejada por aggregate
    private BettingContext betting;

    public void placeBet(Money amount) { ... }
    public void dealInitialCards(Deck deck) { ... }
    public void playerHit(Hand hand, Card card) { ... }
    public void playerStand(Hand hand) { ... }
    public void doubleDown(Hand hand, Card card) { ... }
    public Hand split(Hand hand, Card card1, Card card2) { ... }
    public void resolve(GameResult result) { ... }
    // Métodos que encapsulan reglas de negocio
}
```

**Agregar en el dominio:**
- `GameStatus` enum con comportamiento
- `Hand` entity con métodos `addCard()`, `calculateScore()`, `canSplit()`
- `GameResult` enum
- `BettingContext` value object

---

### 3. Refactorizar Value Object Card

**Cambio:** `model/valueobject/Card` → `domain/model/game/Card.java`

```java
public class Card {
    private Suit suit;
    private Rank rank;
    private CardValue value;        // Value Object para valor inteligente

    public int getScoreValue(boolean withAceAdjusted) { ... }
    public boolean isAce() { ... }
    public boolean isFaceCard() { ... }
    public boolean matchesRank(Card other) { ... } // Para split
}
```

**Agregar:**
- `CardValue` value object que conoce cómo calcular su valor según contexto
- Méodos de dominio en el objeto

---

### 4. Crear Domain Services

**Cambio:** Extraer lógica de negocio de `GameService` a servicios de dominio puros

```java
// BlackjackScoringService.java
public class BlackjackScoringService {
    public int calculateHandScore(Hand hand) {
        // Lógica de puntuación con As flexible
    }

    public boolean isBlackjack(Hand hand) { ... }
    public boolean isBust(Hand hand) { ... }
}

// GameRulesService.java
public class GameRulesService {
    public boolean canSplit(Hand hand) { ... }
    public boolean canDoubleDown(Hand hand) { ... }
    public boolean canBuyInsurance(Game game) { ... }
    public boolean canHit(Hand hand) { ... }
}

// BettingService.java
public class BettingService {
    public Money calculateWinnings(GameResult result, Money bet) { ... }
    public Money calculateInsurancePayout(Money insuranceBet) { ... }
}
```

---

### 5. Definir Ports (Interfaces)

**Cambio:** Crear interfaces que definen los contratos

```java
// Puerto de entrada (Use Cases)
public interface CreateGameUseCase {
    Mono<Game> execute(PlayerId playerId);
}

public interface PlaceBetUseCase {
    Mono<Game> execute(GameId gameId, Money bet);
}

public interface PlayerActionUseCase {
    Mono<Game> executeAction(GameId gameId, PlayerId playerId, PlayerAction action);
}

public interface RankingUseCase {
    Mono<List<Player>> execute();
}

// Puerto de salida (Repository interfaces)
public interface PlayerRepositoryPort {
    Mono<Player> save(Player player);
    Mono<Player> findById(PlayerId id);
    Mono<Player> findByUsername(Username username);
    Flux<Player> findAllOrderByBalanceDesc();
}

public interface GameRepositoryPort {
    Mono<Game> save(Game game);
    Mono<Game> findById(GameId id);
    Flux<Game> findByPlayerId(PlayerId playerId);
}
```

---

### 6. Crear Application Services

**Cambio:** Los controllers llaman a Application Services que orquestan Use Cases

```java
// GameApplicationService.java
@Service
public class GameApplicationService {

    private final CreateGameUseCase createGameUseCase;
    private final PlaceBetUseCase placeBetUseCase;
    private final PlayerActionUseCase playerActionUseCase;

    @Autowired
    public GameApplicationService(
            CreateGameUseCase createGameUseCase,
            PlaceBetUseCase placeBetUseCase,
            PlayerActionUseCase playerActionUseCase) {
        this.createGameUseCase = createGameUseCase;
        this.placeBetUseCase = placeBetUseCase;
        this.playerActionUseCase = playerActionUseCase;
    }

    public Mono<GameResponse> createGame(PlayerId playerId) {
        return createGameUseCase.execute(playerId)
                .map(this::toResponse);
    }

    public Mono<GameResponse> play(GameId gameId, PlayerId playerId, PlayRequest request) {
        // Orquestar use cases según acción
    }
}
```

---

### 7. Crear Infrastructure Adapters

**Cambio:** Implementar los puertos de salida

```java
// PlayerRepositoryAdapter.java
@Repository
public class PlayerRepositoryAdapter implements PlayerRepositoryPort {

    private final PlayerReactiveRepository repository;
    private final PlayerMapper mapper;

    @Override
    public Mono<Player> save(Player player) {
        PlayerEntity entity = mapper.toEntity(player);
        return repository.save(entity)
                .map(mapper::toDomain);
    }

    // ... implementaciones de otros métodos
}
```

---

### 8. Separar DTOs de API vs DTOs de Aplicación

**Cambios:**
- `infrastructure/api/dto/` - DTOs que reciben los controllers (serialización HTTP)
- `application/dto/` - DTOs internos para comunicación entre capas
- `domain/` - No tiene DTOs, trabaja con modelos del dominio

---

### 9. Migrar excepciones a dominio

**Cambio:** Mover desde `exception/` a `domain/exceptions/`

```java
// Domain exceptions (sin dependencias de infraestructura)
public class InsufficientFundsException extends DomainException {
    public InsufficientFundsException(Money required, Money available) { ... }
}

public class GameRuleViolationException extends DomainException {
    public GameRuleViolationException(String rule, String message) { ... }
}
```

---

### 10. Crear Factories para agregados

```java
// GameFactory.java
@Component
public class GameFactory {
    public Game createNewGame(Player player) {
        return Game.create(player.getId());
    }
}

// DeckFactory.java
public class DeckFactory {
    public Deck createShuffledDeck() {
        List<Card> cards = new ArrayList<>();
        for (Suit suit : Suit.values()) {
            for (Rank rank : Rank.values()) {
                cards.add(new Card(suit, rank));
            }
        }
        Collections.shuffle(cards);
        return new Deck(cards);
    }
}
```

---

## Resumen de Cambios por Prioridad

### Fase 1: Core Domain (Sin dependencias externas)
1. Crear Value Objects (`Money`, `PlayerId`, `Username`, `CardValue`)
2. Crear modelo rico `Player` con comportamiento
3. Crear modelo rico `Game` con comportamiento
4. Extraer `Card` a dominio
5. Crear Domain Services

### Fase 2: Ports e Interfaces
6. Definir puertos de entrada (Use Cases)
7. Definir puertos de salida (Repository interfaces)

### Fase 3: Application Layer
8. Crear Application Services
9. Crear mapeadores

### Fase 4: Infrastructure
10. Implementar Repository Adapters
11. Refactorizar Controllers
12. Mantener security config

---

## Beneficios Esperados

1. **Modelo rico**: La lógica de negocio vive en los modelos, no en servicios
2. **Testabilidad**: Domain Services sin dependencias externas son fácilmente testables
3. **Flexibilidad**: Los puertos permiten cambiar implementación de infraestructura
4. **Claridad**: Estructura revela intención y responsabilidad de cada componente
5. **Evolución**: Agregados bien definidos facilitan cambios y extensiones

---

## Archivos a eliminar/mover

- `model/entity/PlayerEntity.java` → eliminar (reemplazado por dominio)
- `model/entity/GameEntity.java` → eliminar
- `model/entity/HandEntity.java` → eliminar
- `model/entity/CardEntity.java` → mover a infrastructure
- `model/entity/DeckEntity.java` → mover a infrastructure
- `service/GameService.java` → refactorizar a Application Service
- `service/PlayerService.java` → refactorizar a Application Service
- `service/DeckService.java` → eliminar (lógica en dominio/factory)
- `service/AuthService.java` → refactorizar a Application Service

---

*Documento de planificación para refactorización DDD Hexagonal*
*Fecha: 2026-05-07*