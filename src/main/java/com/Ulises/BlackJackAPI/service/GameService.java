package com.Ulises.BlackJackAPI.service;

import com.Ulises.BlackJackAPI.domain.entity.CardEntity;
import com.Ulises.BlackJackAPI.domain.entity.GameEntity;
import com.Ulises.BlackJackAPI.domain.entity.HandEntity;
import com.Ulises.BlackJackAPI.domain.enums.GameResult;
import com.Ulises.BlackJackAPI.domain.enums.GameStatus;
import com.Ulises.BlackJackAPI.domain.enums.HandType;
import com.Ulises.BlackJackAPI.domain.factory.CardFactory;
import com.Ulises.BlackJackAPI.domain.services.GameRulesEngine;
import com.Ulises.BlackJackAPI.domain.services.ScoreCalculator;
import com.Ulises.BlackJackAPI.dto.CardResponse;
import com.Ulises.BlackJackAPI.dto.GameResponse;
import com.Ulises.BlackJackAPI.dto.HandResponse;
import com.Ulises.BlackJackAPI.dto.PlayRequest;
import com.Ulises.BlackJackAPI.exception.GameNotFoundException;
import com.Ulises.BlackJackAPI.exception.InvalidMoveException;
import com.Ulises.BlackJackAPI.repository.CardRepository;
import com.Ulises.BlackJackAPI.repository.GameRepository;
import com.Ulises.BlackJackAPI.repository.HandRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Service for managing blackjack game operations.
 * Handles game creation, gameplay actions (hit, stand, double, split), and game resolution.
 *
 * @author Ulises Lafuente
 */
@Service
public class GameService {

    private final GameRepository gameRepository;
    private final HandRepository handRepository;
    private final CardRepository cardRepository;
    private final DeckService deckService;
    private final PlayerService playerService;
    private final GameRulesEngine rulesEngine;
    private final ScoreCalculator scoreCalculator;
    private final CardFactory cardFactory;

    public GameService(
            GameRepository gameRepository,
            HandRepository handRepository,
            CardRepository cardRepository,
            DeckService deckService,
            PlayerService playerService,
            GameRulesEngine rulesEngine,
            ScoreCalculator scoreCalculator,
            CardFactory cardFactory) {
        this.gameRepository = gameRepository;
        this.handRepository = handRepository;
        this.cardRepository = cardRepository;
        this.deckService = deckService;
        this.playerService = playerService;
        this.rulesEngine = rulesEngine;
        this.scoreCalculator = scoreCalculator;
        this.cardFactory = cardFactory;
    }

    public Mono<GameResponse> createGame(Long playerId) {
        GameEntity game = new GameEntity(playerId);
        return gameRepository.save(game)
                .flatMap(saved -> deckService.createDeckForGame(saved.getId()).thenReturn(saved))
                .flatMap(this::mapToGameResponse);
    }

    public Mono<GameResponse> getGame(Long gameId, Long playerId) {
        return gameRepository.findByIdAndPlayerId(gameId, playerId)
                .switchIfEmpty(Mono.error(new GameNotFoundException("Game not found")))
                .flatMap(game -> loadGameDetails(game, true));
    }

    public Mono<GameResponse> play(Long gameId, Long playerId, PlayRequest request) {
        return gameRepository.findByIdAndPlayerId(gameId, playerId)
                .switchIfEmpty(Mono.error(new GameNotFoundException("Game not found")))
                .flatMap(game -> processMove(game, playerId, request));
    }

    public Mono<Void> deleteGame(Long gameId, Long playerId) {
        return gameRepository.findByIdAndPlayerId(gameId, playerId)
                .switchIfEmpty(Mono.error(new GameNotFoundException("Game not found")))
                .flatMap(game -> {
                    game.setStatus(GameStatus.FINISHED);
                    game.setResult(GameResult.LOSE);
                    return gameRepository.save(game);
                })
                .then();
    }

    private Mono<GameResponse> processMove(GameEntity game, Long playerId, PlayRequest request) {
        String action = request.getAction() != null ? request.getAction().toUpperCase() : null;

        return switch (action) {
            case "BET" -> placeBet(game, playerId, request.getBet());
            case "INSURANCE" -> buyInsurance(game, playerId);
            case "HIT" -> hit(game, request.getHandIndex());
            case "STAND" -> stand(game, request.getHandIndex());
            case "DOUBLE" -> doubleDown(game, playerId, request.getHandIndex());
            case "SPLIT" -> split(game, playerId);
            default -> Mono.error(new InvalidMoveException("Invalid action: " + action));
        };
    }

    private Mono<GameResponse> placeBet(GameEntity game, Long playerId, Integer betAmount) {
        if (game.getStatus() != GameStatus.BETTING) {
            return Mono.error(new InvalidMoveException("Betting phase is over"));
        }
        if (betAmount == null || betAmount <= 0) {
            return Mono.error(new InvalidMoveException("Invalid bet amount"));
        }

        return playerService.getPlayerEntityById(playerId)
                .flatMap(player -> {
                    if (player.getScore() < betAmount) {
                        return Mono.error(new InvalidMoveException("Insufficient funds"));
                    }
                    game.setBet(betAmount);
                    game.setStatus(GameStatus.PLAYER_TURN);
                    return gameRepository.save(game);
                })
                .flatMap(saved -> dealInitialCards(saved).thenReturn(saved))
                .flatMap(this::checkForBlackjack);
    }

    private Mono<Void> dealInitialCards(GameEntity game) {
        HandEntity playerHand = new HandEntity(game.getId(), HandType.PLAYER, 0);
        HandEntity croupierHand = new HandEntity(game.getId(), HandType.CROUPIER, 0);

        return handRepository.save(playerHand)
                .flatMap(ph -> handRepository.save(croupierHand)
                        .flatMap(ch -> deckService.drawCards(game.getId(), 4)
                                .collectList()
                                .flatMap(cards -> {
                                    if (cards.size() < 4) {
                                        return Mono.error(new InvalidMoveException("Not enough cards in deck"));
                                    }
                                    var playerCard1 = cards.get(0);
                                    var playerCard2 = cards.get(1);
                                    var croupierCard1 = cards.get(2);
                                    var croupierCard2 = cards.get(3);

                                    CardEntity pc1 = cardFactory.createCardEntity(ph, playerCard1.suit(), playerCard1.rank(), false);
                                    CardEntity pc2 = cardFactory.createCardEntity(ph, playerCard2.suit(), playerCard2.rank(), false);
                                    CardEntity cc1 = cardFactory.createCardEntity(ch, croupierCard1.suit(), croupierCard1.rank(), false);
                                    CardEntity cc2 = cardFactory.createCardEntity(ch, croupierCard2.suit(), croupierCard2.rank(), true);

                                    return cardRepository.saveAll(List.of(pc1, pc2, cc1, cc2)).then();
                                })));
    }

    private Mono<GameResponse> checkForBlackjack(GameEntity game) {
        return handRepository.findByGameIdAndTypeAndHandIndex(game.getId(), HandType.PLAYER, 0)
                .flatMap(playerHand -> cardRepository.findByHandIdOrderById(playerHand.getId())
                        .collectList()
                        .flatMap(cards -> {
int score = scoreCalculator.calculateHandScore(cards);
                            playerHand.setScore(score);
                            return handRepository.save(playerHand)
                                    .flatMap(saved -> {
                                        if (rulesEngine.checkBlackjack(saved)) {
                                            return processBlackjack(game);
                                        }
                                        return checkInsuranceOption(game);
                                    });
                        }));
    }


    private Mono<GameResponse> checkInsuranceOption(GameEntity game) {
        return handRepository.findByGameIdAndTypeAndHandIndex(game.getId(), HandType.CROUPIER, 0)
                .flatMap(croupierHand -> cardRepository.findByHandIdOrderById(croupierHand.getId())
                        .take(1)
                        .last()
                        .map(card -> card.getRank() == com.Ulises.BlackJackAPI.domain.enums.Rank.ACE)
                        .defaultIfEmpty(false))
                .flatMap(canInsurance -> {
                    if (Boolean.TRUE.equals(canInsurance)) {
                        return mapToGameResponseWithMessage(game, "Insurance available");
                    }
                    return croupierTurn(game).onErrorResume(e -> mapToGameResponse(game));
                })
                .onErrorResume(e -> croupierTurn(game));
    }

    private Mono<GameResponse> processBlackjack(GameEntity game) {
        return handRepository.findByGameIdAndTypeAndHandIndex(game.getId(), HandType.CROUPIER, 0)
                .flatMap(croupierHand -> cardRepository.findByHandIdOrderById(croupierHand.getId())
                        .collectList()
                        .flatMap(croupierCards -> {
                            int croupierScore = scoreCalculator.calculateHandScore(croupierCards);
                            croupierHand.setScore(croupierScore);
                            return handRepository.save(croupierHand);
                        })
                        .flatMap(saved -> revealCroupierCard(game)
                                .then(finalizeGame(game, GameResult.BLACKJACK))));
    }

    private Mono<Void> revealCroupierCard(GameEntity game) {
        return handRepository.findByGameIdAndTypeAndHandIndex(game.getId(), HandType.CROUPIER, 0)
                .flatMap(hand -> cardRepository.findByHandIdOrderById(hand.getId())
                        .filter(card -> card.getIsHidden())
                        .flatMap(card -> {
                            card.setIsHidden(false);
                            return cardRepository.save(card);
                        })
                        .then());
    }

    private Mono<GameResponse> buyInsurance(GameEntity game, Long playerId) {
        if (game.getStatus() != GameStatus.PLAYER_TURN) {
            return Mono.error(new InvalidMoveException("Cannot buy insurance now"));
        }
        if (game.getInsuranceBet() > 0) {
            return Mono.error(new InvalidMoveException("Insurance already bought"));
        }

        int insuranceAmount = game.getBet() / 2;
        return playerService.getPlayerEntityById(playerId)
                .flatMap(player -> {
                    if (player.getScore() < insuranceAmount) {
                        return Mono.error(new InvalidMoveException("Insufficient funds for insurance"));
                    }
                    game.setInsuranceBet(insuranceAmount);
                    return gameRepository.save(game);
                })
                .flatMap(saved -> croupierTurn(saved));
    }

    private Mono<GameResponse> hit(GameEntity game, Integer handIndex) {
        if (game.getStatus() != GameStatus.PLAYER_TURN) {
            return Mono.error(new InvalidMoveException("Not your turn"));
        }

        int idx = handIndex != null ? handIndex : 0;
        return handRepository.findByGameIdAndTypeAndHandIndex(game.getId(), HandType.PLAYER, idx)
                .switchIfEmpty(Mono.error(new InvalidMoveException("Hand not found")))
                .flatMap(hand -> deckService.drawCard(game.getId())
                        .flatMap(card -> {
                            CardEntity cardEntity = cardFactory.createCardEntity(hand, card.suit(), card.rank(), false);
                            return cardRepository.save(cardEntity)
                                    .flatMap(saved -> updateHandScore(game, hand));
                        }));
    }

    private Mono<GameResponse> stand(GameEntity game, Integer handIndex) {
        if (game.getStatus() != GameStatus.PLAYER_TURN) {
            return Mono.error(new InvalidMoveException("Not your turn"));
        }

        int idx = handIndex != null ? handIndex : 0;
        return handRepository.findByGameIdAndTypeAndHandIndex(game.getId(), HandType.PLAYER, idx)
                .switchIfEmpty(Mono.error(new InvalidMoveException("Hand not found")))
                .flatMap(hand -> checkAllHandsPlayed(game, hand));
    }

    private Mono<GameResponse> checkAllHandsPlayed(GameEntity game, HandEntity lastHand) {
        return handRepository.findByGameId(game.getId())
                .collectList()
                .flatMap(allHands -> {
                    if (rulesEngine.allPlayerHandsPlayed(allHands, lastHand)) {
                        return croupierTurn(game);
                    }
                    return mapToGameResponse(game);
                });
    }

    private Mono<GameResponse> doubleDown(GameEntity game, Long playerId, Integer handIndex) {
        if (game.getStatus() != GameStatus.PLAYER_TURN) {
            return Mono.error(new InvalidMoveException("Not your turn"));
        }

        int idx = handIndex != null ? handIndex : 0;
        int additionalBet = game.getBet();

        return playerService.getPlayerEntityById(playerId)
                .flatMap(player -> {
                    if (player.getScore() < additionalBet) {
                        return Mono.error(new InvalidMoveException("Insufficient funds to double"));
                    }
                    return handRepository.findByGameIdAndTypeAndHandIndex(game.getId(), HandType.PLAYER, idx);
                })
                .switchIfEmpty(Mono.error(new InvalidMoveException("Hand not found")))
                .flatMap(hand -> {
                    game.setBet(game.getBet() * 2);
                    return gameRepository.save(game)
                            .flatMap(saved -> deckService.drawCard(game.getId())
                                    .flatMap(card -> {
                                        CardEntity cardEntity = cardFactory.createCardEntity(hand, card.suit(), card.rank(), false);
                                        return cardRepository.save(cardEntity)
                                                .flatMap(savedCard -> updateHandScoreAfterAction(game, hand));
                                    }));
                });
    }

    private Mono<GameResponse> split(GameEntity game, Long playerId) {
        if (game.getStatus() != GameStatus.PLAYER_TURN) {
            return Mono.error(new InvalidMoveException("Not your turn"));
        }

        return handRepository.findByGameIdAndTypeAndHandIndex(game.getId(), HandType.PLAYER, 0)
                .flatMap(originalHand -> cardRepository.findByHandIdOrderById(originalHand.getId())
                        .collectList()
                        .flatMap(cards -> {
                            if (cards.size() != 2 || cards.get(0).getRank() != cards.get(1).getRank()) {
                                return Mono.error(new InvalidMoveException("Cannot split: need two cards of same rank"));
                            }
                            return playerService.getPlayerEntityById(playerId);
                        })
                        .flatMap(player -> {
                            if (player.getScore() < game.getBet()) {
                                return Mono.error(new InvalidMoveException("Insufficient funds to split"));
                            }
                            return performSplit(game);
                        }));
    }

    private Mono<GameResponse> performSplit(GameEntity game) {
        return handRepository.findByGameIdAndTypeAndHandIndex(game.getId(), HandType.PLAYER, 0)
                .flatMap(originalHand -> cardRepository.findByHandIdOrderById(originalHand.getId())
                        .collectList()
                        .flatMap(cards -> {
                            var card1 = cards.get(0);
                            var card2 = cards.get(1);

                            originalHand.setScore(card1.getValue());

                            HandEntity newHand = new HandEntity(game.getId(), HandType.PLAYER, 1);
                            newHand.setScore(card2.getValue());

                            return handRepository.save(originalHand)
                                    .flatMap(h1 -> handRepository.save(newHand)
                                            .flatMap(h2 -> deckService.drawCards(game.getId(), 2)
                                                    .collectList()
                                                    .flatMap(drawnCards -> {
                                                        if (drawnCards.size() < 2) {
                                                            return Mono.error(new InvalidMoveException("Not enough cards to complete split"));
                                                        }
                                                        CardEntity c1 = cardFactory.createCardEntity(h1, card1.getSuit(), card1.getRank(), false);
                                                        CardEntity c2 = cardFactory.createCardEntity(h2, card2.getSuit(), card2.getRank(), false);
                                                        CardEntity c3 = cardFactory.createCardEntity(h1, drawnCards.get(0).suit(), drawnCards.get(0).rank(), false);
                                                        CardEntity c4 = cardFactory.createCardEntity(h2, drawnCards.get(1).suit(), drawnCards.get(1).rank(), false);
                                                        return cardRepository.saveAll(List.of(c1, c2, c3, c4)).then();
                                                    })
                                            )
                                    );
                        }))
                .then(loadGameDetails(game, false))
                .map(resp -> {
                    resp.setMessage("Split completed. Play each hand.");
                    return resp;
                });
    }

    private Mono<GameResponse> updateHandScore(GameEntity game, HandEntity hand) {
        return cardRepository.findByHandIdOrderById(hand.getId())
                .collectList()
                .flatMap(cards -> {
                    int score = scoreCalculator.calculateHandScore(cards);
                    hand.setScore(score);
                    return handRepository.save(hand);
                })
                .flatMap(saved -> {
                    if (saved.getScore() > 21) {
                        return checkAllHandsPlayed(game, saved);
                    }
                    return croupierTurn(game).onErrorResume(e -> loadGameDetails(game, false));
                });
    }

    private Mono<GameResponse> updateHandScoreAfterAction(GameEntity game, HandEntity hand) {
        return cardRepository.findByHandIdOrderById(hand.getId())
                .collectList()
                .flatMap(cards -> {
                    int score = scoreCalculator.calculateHandScore(cards);
                    hand.setScore(score);
                    return handRepository.save(hand);
                })
                .flatMap(saved -> {
                    if (saved.getScore() > 21) {
                        return checkAllHandsPlayed(game, saved);
                    }
                    return croupierTurn(game);
                });
    }

    private Mono<GameResponse> croupierTurn(GameEntity game) {
        game.setStatus(GameStatus.CROUPIER_TURN);
        return gameRepository.save(game)
                .flatMap(saved -> revealCroupierCard(saved)
                        .then(handRepository.findByGameIdAndTypeAndHandIndex(game.getId(), HandType.CROUPIER, 0))
                        .flatMap(croupierHand -> handleDealerTurn(croupierHand, saved)));
    }

    private Mono<GameResponse> handleDealerTurn(HandEntity croupierHand, GameEntity game) {
        return cardRepository.findByHandIdOrderById(croupierHand.getId())
                .collectList()
                .flatMap(cards -> {
                    int score = scoreCalculator.calculateHandScore(cards);
                    croupierHand.setScore(score);
                    return handRepository.save(croupierHand);
                })
                .flatMap(hand -> {
                    if (scoreCalculator.shouldDealerHit(hand)) {
                        return deckService.drawCard(game.getId())
                                .flatMap(card -> {
                                    CardEntity cardEntity = cardFactory.createCardEntity(hand, card.suit(), card.rank(), false);
                                    return cardRepository.save(cardEntity)
                                            .flatMap(saved -> handleDealerTurn(hand, game));
                                });
                    }
                    return resolveGame(game);
                });
    }

    private Mono<GameResponse> resolveGame(GameEntity game) {
        return handRepository.findByGameIdAndTypeAndHandIndex(game.getId(), HandType.PLAYER, 0)
                .flatMap(playerHand -> handRepository.findByGameIdAndTypeAndHandIndex(game.getId(), HandType.CROUPIER, 0)
                        .map(croupierHand -> {
                            GameResult result = rulesEngine.determineResult(playerHand.getScore(), croupierHand.getScore());
                            return result;
                        })
                        .flatMap(result -> finalizeGame(game, result)));
    }

    private Mono<GameResponse> finalizeGame(GameEntity game, GameResult result) {
        game.setResult(result);
        game.setStatus(GameStatus.FINISHED);

        return handRepository.findByGameIdAndTypeAndHandIndex(game.getId(), HandType.PLAYER, 0)
                .flatMap(playerHand -> handRepository.findByGameIdAndTypeAndHandIndex(game.getId(), HandType.CROUPIER, 0)
                        .flatMap(croupierHand -> {
                            game.setPlayerScore(playerHand.getScore());
                            game.setCroupierScore(croupierHand.getScore());
                            return gameRepository.save(game);
                        }))
                .flatMap(saved -> playerService.updateScore(game.getPlayerId(), result, game.getBet(), game.getInsuranceBet()))
                .then(loadGameDetails(game, false).map(r -> r));
    }

    private Mono<GameResponse> loadGameDetails(GameEntity game, boolean revealCroupier) {
        return handRepository.findByGameId(game.getId())
                .flatMap(hand -> cardRepository.findByHandIdOrderById(hand.getId())
                        .collectList()
                        .map(cards -> {
                            HandWithCards hwc = new HandWithCards();
                            hwc.hand = hand;
                            hwc.cards = cards;
                            return hwc;
                        }))
                .collectList()
                .map(handCardsList -> mapToGameResponseWithHands(game, handCardsList, revealCroupier));
    }

    private Mono<GameResponse> mapToGameResponse(GameEntity game) {
        return mapToGameResponseWithMessage(game, null);
    }

    private Mono<GameResponse> mapToGameResponseWithMessage(GameEntity game, String message) {
        return loadGameDetails(game, false)
                .map(resp -> {
                    resp.setMessage(message);
                    return resp;
                });
    }

    private GameResponse mapToGameResponseWithHands(GameEntity game, List<HandWithCards> handCardsList, boolean revealCroupier) {
        List<HandResponse> hands = handCardsList.stream()
                .map(pair -> {
                    HandEntity hand = pair.hand;
                    List<CardEntity> cards = pair.cards;

                    List<CardResponse> cardResponses = cards.stream()
                            .map(c -> {
                                boolean isHidden = c.getIsHidden();
                                if (hand.getType() == HandType.CROUPIER && !revealCroupier) {
                                    List<CardEntity> croupierCards = handCardsList.stream()
                                            .filter(p -> p.hand.getType() == HandType.CROUPIER)
                                            .findFirst()
                                            .map(p -> p.cards)
                                            .orElse(null);
                                    if (croupierCards != null && croupierCards.size() > 1) {
                                        return new CardResponse(c.getSuit(), c.getRank(), c.getValue(), true);
                                    }
                                }
                                return new CardResponse(c.getSuit(), c.getRank(), c.getValue(), isHidden);
                            })
                            .toList();

                    return new HandResponse(hand.getId(), hand.getType(), hand.getHandIndex(), hand.getScore(), cardResponses);
                })
                .toList();

        return new GameResponse(
                game.getId(), game.getPlayerId(), game.getBet(), game.getInsuranceBet(),
                game.getStatus(), game.getResult(), game.getPlayerScore(), game.getCroupierScore(),
                hands, null
        );
    }

    private static class HandWithCards {
        HandEntity hand;
        List<CardEntity> cards;
    }
}