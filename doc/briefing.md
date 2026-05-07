# SPECIFICATION: BlackJack API

## 1. Project Overview

- **Project name**: BlackJackAPI
- **Type**: Reactive REST API with Spring Boot WebFlux + R2DBC
- **Core functionality**: Blackjack card game with player authentication, betting system, and ranking
- **Database**: MySQL (database: `blackjack`, user: `root`, password: `root`)

## 2. Technology Stack

- **Framework**: Spring Boot 4.0.6
- **Web**: Spring WebFlux (reactive)
- **Database**: MySQL with R2DBC (reactive)
- **Security**: Spring Security + JWT Authentication
- **Build**: Maven
- **Java Version**: 21

## 3. Project Structure (by functionality)

```
src/main/java/com/Ulises/BlackJackAPI/
├── config/           # Configuration classes
├── controller/       # REST Controllers
├── service/          # Business logic
├── repository/       # Data access (R2DBC)
├── model/            # Entities and domain objects
├── dto/              # Data Transfer Objects
├── security/         # JWT and security config
├── exception/        # Custom exceptions and handler
└── BlackJackAPIApplication.java
```

## 4. Database Schema

### Tables

**players**
| Column | Type | Description |
|--------|------|-------------|
| id | BIGINT (PK) | Auto-increment |
| username | VARCHAR(50) | Unique, not null |
| password | VARCHAR(255) | BCrypt hashed |
| score | INT | Player's money (default 1000) |
| created_at | TIMESTAMP | Registration date |

**games**
| Column | Type | Description |
|--------|------|-------------|
| id | BIGINT (PK) | Auto-increment |
| player_id | BIGINT (FK) | Reference to player |
| bet | INT | Current bet amount |
| insurance_bet | INT | Insurance bet amount |
| status | VARCHAR(20) | BETTING, PLAYER_TURN, CROUPIER_TURN, FINISHED |
| result | VARCHAR(20) | WIN, LOSE, PUSH, BLACKJACK, INSURANCE_WIN, INSURANCE_LOSE |
| player_score | INT | Final player hand score |
| croupier_score | INT | Final croupier hand score |
| created_at | TIMESTAMP | Game start time |

**hands**
| Column | Type | Description |
|--------|------|-------------|
| id | BIGINT (PK) | Auto-increment |
| game_id | BIGINT (FK) | Reference to game |
| type | VARCHAR(20) | PLAYER or CROUPIER |
| hand_index | INT | For split hands (0, 1) |
| score | INT | Calculated hand score |

**cards**
| Column | Type | Description |
|--------|------|-------------|
| id | BIGINT (PK) | Auto-increment |
| hand_id | BIGINT (FK) | Reference to hand |
| suit | VARCHAR(20) | HEARTS, DIAMONDS, CLUBS, SPADES |
| rank | VARCHAR(10) | ACE, TWO, THREE, ..., TEN, JACK, QUEEN, KING |
| value | INT | Card value (1-10, 11 for Ace) |
| is_hidden | BOOLEAN | True for croupier's hidden card |

**decks** (shuffled deck per game)
| Column | Type | Description |
|--------|------|-------------|
| id | BIGINT (PK) | Auto-increment |
| game_id | BIGINT (FK) | Reference to game |
| suit | VARCHAR(20) | Card suit |
| rank | VARCHAR(10) | Card rank |
| value | INT | Card value |
| drawn | BOOLEAN | True if card was drawn |

## 5. Entities and Value Objects

### Entities

**Player** (model/entity/Player.java)
- id: Long
- username: String
- password: String (BCrypt encoded)
- score: Integer (default 1000)
- createdAt: LocalDateTime

**Game** (model/entity/Game.java)
- id: Long
- player: Player
- bet: Integer
- insuranceBet: Integer
- status: GameStatus (enum)
- result: GameResult (enum)
- playerScore: Integer
- croupierScore: Integer
- hands: List<Hand>
- deck: List<Card>
- createdAt: LocalDateTime

**Hand** (model/entity/Hand.java)
- id: Long
- game: Game
- type: HandType (PLAYER, CROUPIER)
- handIndex: Integer
- cards: List<Card>
- score: Integer

### Value Objects

**Card** (model/valueobject/Card.java)
- suit: Suit (HEARTS, DIAMONDS, CLUBS, SPADES)
- rank: Rank (ACE, TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE, TEN, JACK, QUEEN, KING)
- value: Integer (1-10, 11 for Ace)

### Enums

**GameStatus**
- BETTING
- PLAYER_TURN
- CROUPIER_TURN
- FINISHED

**GameResult**
- WIN
- LOSE
- PUSH (tie)
- BLACKJACK
- INSURANCE_WIN
- INSURANCE_LOSE
- PLAYER_BUST (player exceeded 21)

**HandType**
- PLAYER
- CROUPIER

**Suit**
- HEARTS
- DIAMONDS
- CLUBS
- SPADES

**Rank**
- ACE, TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE, TEN, JACK, QUEEN, KING

## 6. Authentication (JWT)

### Endpoints

**POST /auth/register**
- Body: `{ "username": "string", "password": "string" }`
- Response: 201 Created with player data

**POST /auth/login**
- Body: `{ "username": "string", "password": "string" }`
- Response: 200 OK with JWT token `{ "token": "jwt-string" }`

### JWT Configuration
- Token expiration: 24 hours
- Secret key: Configured in application.properties
- All `/game/**`, `/player/**`, `/ranking` endpoints require valid JWT

## 7. Game Endpoints

### POST /game/new
- Description: Create new game
- Auth: JWT required
- Request body: (empty - uses authenticated player)
- Response: 201 Created with game details

### GET /game/{id}
- Description: Get game details
- Auth: JWT required
- Path variable: game id
- Response: 200 OK with game state

### POST /game/{id}/play
- Description: Make a move in the game
- Auth: JWT required
- Path variable: game id
- Request body:
  ```json
  {
    "action": "HIT | STAND | DOUBLE | SPLIT | INSURANCE",
    "bet": 100  // only for initial bet in BETTING state
  }
  ```
- Response: 200 OK with updated game state

### DELETE /game/{id}/delete
- Description: Delete/abandon game
- Auth: JWT required
- Path variable: game id
- Response: 204 No Content

### GET /ranking
- Description: Get player rankings
- Auth: JWT required
- Response: 200 OK with list sorted by score (descending)

### PUT /player/{playerId}
- Description: Change player name
- Auth: JWT required
- Path variable: player id
- Request body: `{ "username": "new-name" }`
- Response: 200 OK with updated player

## 8. Game Logic

### Card Values
- **Ace**: 11 or 1 (auto-adjusts to 1 if total > 21)
- **2-10**: Face value
- **Jack, Queen, King**: 10

### Game Flow

1. **Betting Phase**
   - Player places bet (POST /game/{id}/play with action: BET, bet: amount)
   - Minimum bet: 1
   - Maximum bet: player's current score

2. **Initial Deal**
   - Player receives 2 cards (both visible)
   - Croupier receives 2 cards (one visible, one hidden)
   - If player has Blackjack (21 with 2 cards), immediately win 3:2 unless croupier also has Blackjack

3. **Insurance Option**
   - If croupier's visible card is Ace, player can buy insurance
   - Insurance bet = half of original bet
   - Insurance pays 2:1 if croupier has Blackjack

4. **Player Turn**
   - Options based on hand:
     - **HIT**: Draw another card
     - **STAND**: End player turn
     - **DOUBLE**: Double bet, draw one card, stand
     - **SPLIT**: If two cards of same rank, split into two hands (requires additional bet equal to original)
   - If hand score > 21, player busts and loses

5. **Croupier Turn**
   - If player busted, croupier wins automatically
   - Croupier reveals hidden card
   - Croupier draws until score >= 17
   - Croupier stands on 17 or higher

6. **Resolution**
   - Compare scores:
     - Player > Croupier (≤21): Player wins, receives 3:2
     - Player < Croupier: Player loses, bet deducted
     - Player = Croupier: Push, bet returned
     - Croupier > 21: Croupier busts, player wins

7. **Score Update**
   - Win: score += bet * 1.5
   - Lose: score -= bet
   - Push: no change
   - Insurance win: score += insuranceBet * 2
   - Insurance lose: score -= insuranceBet

### Split Logic
- Only available when first two cards have same rank
- Creates two separate hands
- Each hand gets one additional card
- Player plays each hand sequentially
- Cannot split again after split

## 9. Response Format

All responses follow standard format:
```json
{
  "code": 200,
  "message": "Success",
  "data": { ... }
}
```

Error responses:
```json
{
  "code": 400,
  "message": "Error description",
  "data": null
}
```

## 10. Exception Handling

- **GlobalExceptionHandler**: @ControllerAdvice for centralized error handling
- Custom exceptions:
  - `PlayerNotFoundException`
  - `GameNotFoundException`
  - `InvalidMoveException`
  - `InsufficientFundsException`
  - `AuthenticationException`

## 11. Configuration (application.properties)

```properties
spring.r2dbc.url=r2dbc:mysql://localhost:3306/blackjack
spring.r2dbc.username=root
spring.r2dbc.password=root

jwt.secret=your-256-bit-secret-key-here
jwt.expiration=86400000

server.port=8080
```

## 12. Testing Requirements

### Unit Tests (required for services)
- DeckServiceTest: shuffling, drawing cards
- GameServiceTest: game logic, scoring
- PlayerServiceTest: score calculation
- AuthServiceTest: JWT token generation/validation

### Test Coverage Goals
- Core game logic: 80%+
- Service layer: 80%+

## 13. Acceptance Criteria

1. Player can register and login with JWT
2. Player can create new game and place bet
3. Game correctly deals initial cards (player 2 visible, croupier 1 visible + 1 hidden)
4. All player actions work: HIT, STAND, DOUBLE, SPLIT, INSURANCE
5. Croupier follows rules (draw on ≤16, stand on ≥17)
6. Ace auto-adjusts from 11 to 1 when score > 21
7. Score updates correctly after each game (win/lose/push)
8. Ranking returns players sorted by score
9. All endpoints require JWT except /auth/**
10. API returns consistent {code, message, data} format

---

*Document version: 1.0*
*Last updated: 2026-05-07*