# Manual de Usuario - BlackJackAPI

## Índice

1. [Introducción](#introducción)
2. [Requisitos](#requisitos)
3. [Registro e Inicio de Sesión](#registro-e-inicio-de-sesión)
4. [Crear una Partida](#crear-una-partida)
5. [Jugadas Disponibles](#jugadas-disponibles)
6. [Fases del Juego](#fases-del-juego)
7. [Sistema de Puntuación](#sistema-de-puntuación)
8. [Ranking de Jugadores](#ranking-de-jugadores)

---

## Introducción

BlackJackAPI es una API REST que permite jugar al Blackjack contra el crupier. Cada jugador comienza con 1000 puntos y puede ganar o perder puntos según el resultado de las partidas.

## Requisitos

Para ejecutar la aplicación necesitas:

1. **MySQL 8.0+** funcionando (puede usar Docker: `docker-compose up -d`)
2. **Java 21**
3. **Maven 3.9+**

Ejecutar: `./mvnw spring-boot:run`

> **Nota**: La base de datos debe estar creada con el schema en `src/main/resources/schema.sql`

## Registro e Inicio de Sesión

### Registro

```bash
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username": "jugador1", "password": "contraseña123"}'
```

Respuesta:
```json
{
  "code": 200,
  "message": "Player registered successfully",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "playerId": 1,
    "username": "jugador1",
    "score": 1000
  }
}
```

### Inicio de Sesión

```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "jugador1", "password": "contraseña123"}'
```

> **Importante**: Guarda el token devuelto. Lo necesitarás para todas las demás peticiones en el header `Authorization: Bearer <token>`.

---

## Crear una Partida

```bash
curl -X POST http://localhost:8080/game/new \
  -H "Authorization: Bearer <token>"
```

Respuesta:
```json
{
  "code": 200,
  "message": "Game created successfully",
  "data": {
    "id": 1,
    "playerId": 1,
    "bet": 0,
    "insuranceBet": 0,
    "status": "BETTING",
    "result": null,
    "playerScore": 0,
    "croupierScore": 0,
    "hands": [],
    "message": null
  }
}
```

---

## Jugadas Disponibles

Las jugadas se envían mediante `POST /game/{id}/play` con el siguiente body:

### 1. Apostar (BET)

```json
{
  "action": "BET",
  "bet": 100
}
```

Inicia la partida con la apuesta seleccionada.

### 2. Hit (Tomar carta)

```json
{
  "action": "HIT",
  "handIndex": 0
}
```

Toma una carta adicional. Se puede usar múltiples veces.

### 3. Stand (Plantarse)

```json
{
  "action": "STAND",
  "handIndex": 0
}
```

Se planta y termina el turno para esa mano.

### 4. Double (Doblar)

```json
{
  "action": "DOUBLE",
  "handIndex": 0
}
```

Dobla la apuesta y toma exactamente una carta más.

### 5. Split (Dividir)

```json
{
  "action": "SPLIT"
}
```

Divide la mano en dos si las dos cartas tienen el mismo valor.

### 6. Insurance (Seguro)

```json
{
  "action": "INSURANCE"
}
```

Compra un seguro si el crupier muestra un As (cuesta mitad de la apuesta).

---

## Fases del Juego

1. **BETTING**: Fase inicial. Debes realizar una apuesta con acción `BET`.
2. **PLAYER_TURN**: Turno del jugador. Puedes usar HIT, STAND, DOUBLE, SPLIT, INSURANCE.
3. **CROUPIER_TURN**: El crupier revela su carta y juega automáticamente.
4. **FINISHED**: Partida terminada. Se muestra el resultado final.

---

## Sistema de Puntuación

| Resultado | Puntos |
|-----------|--------|
| WIN | +apuesta × 1.5 |
| BLACKJACK | +apuesta × 1.5 |
| PUSH | 0 (empate) |
| LOSE | -apuesta |
| PLAYER_BUST | -apuesta (te pasas de 21) |
| INSURANCE_WIN | +seguro × 2 |
| INSURANCE_LOSE | -seguro |

**Valor de las cartas**:
- Cartas numéricas (2-10): su valor nominal
- J, Q, K: 10 puntos
- As: 11 puntos (puede reducirse a 1 si te pasas)

---

## Ranking de Jugadores

Ver el ranking de todos los jugadores ordenados por puntuación:

```bash
curl -X GET http://localhost:8080/ranking \
  -H "Authorization: Bearer <token>"
```

---

## Códigos de Estado

| Código | Significado |
|--------|-------------|
| 200 | Éxito |
| 400 | Solicitud inválida |
| 404 | Recurso no encontrado |
| 500 | Error interno del servidor |

## Ejemplo Completo de una Partida

```bash
# 1. Crear partida
curl -X POST http://localhost:8080/game/new -H "Authorization: Bearer TOKEN"

# 2. Apostar 100 puntos
curl -X POST http://localhost:8080/game/1/play \
  -H "Authorization: Bearer TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"action": "BET", "bet": 100}'

# 3. Ver estado
curl -X GET http://localhost:8080/game/1 \
  -H "Authorization: Bearer TOKEN"

# 4. Si tienes 10-11, puedes dobrar
# O simplemente tomar cartas hastaPlantartete
curl -X POST http://localhost:8080/game/1/play \
  -H "Authorization: Bearer TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"action": "HIT", "handIndex": 0}'

# 5. Cuando quieras terminar
curl -X POST http://localhost:8080/game/1/play \
  -H "Authorization: Bearer TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"action": "STAND", "handIndex": 0}'
```

---

**Autor**: Ulises Lafuente
**Versión**: 1.0