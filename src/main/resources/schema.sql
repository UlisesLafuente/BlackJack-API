CREATE DATABASE IF NOT EXISTS blackjack;
USE blackjack;

CREATE TABLE IF NOT EXISTS players (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    score INT DEFAULT 1000,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS games (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    player_id BIGINT NOT NULL,
    bet INT DEFAULT 0,
    insurance_bet INT DEFAULT 0,
    status VARCHAR(20) NOT NULL,
    result VARCHAR(20),
    player_score INT DEFAULT 0,
    croupier_score INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (player_id) REFERENCES players(id) ON DELETE CASCADE
);

CREATE INDEX idx_games_player_id ON games(player_id);
CREATE INDEX idx_games_status ON games(status);
CREATE INDEX idx_games_created_at ON games(created_at);

CREATE TABLE IF NOT EXISTS hands (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    game_id BIGINT NOT NULL,
    type VARCHAR(20) NOT NULL,
    hand_index INT DEFAULT 0,
    score INT DEFAULT 0,
    FOREIGN KEY (game_id) REFERENCES games(id) ON DELETE CASCADE
);

CREATE INDEX idx_hands_game_id ON hands(game_id);
CREATE INDEX idx_hands_game_type ON hands(game_id, type);

CREATE TABLE IF NOT EXISTS cards (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    hand_id BIGINT NOT NULL,
    suit VARCHAR(20) NOT NULL,
    card_rank VARCHAR(10) NOT NULL,
    value INT NOT NULL,
    is_hidden BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (hand_id) REFERENCES hands(id) ON DELETE CASCADE
);

CREATE INDEX idx_cards_hand_id ON cards(hand_id);

CREATE TABLE IF NOT EXISTS decks (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    game_id BIGINT NOT NULL,
    suit VARCHAR(20) NOT NULL,
    card_rank VARCHAR(10) NOT NULL,
    value INT NOT NULL,
    drawn BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (game_id) REFERENCES games(id) ON DELETE CASCADE
);

CREATE INDEX idx_decks_game_id ON decks(game_id);
CREATE INDEX idx_decks_game_drawn ON decks(game_id, drawn);