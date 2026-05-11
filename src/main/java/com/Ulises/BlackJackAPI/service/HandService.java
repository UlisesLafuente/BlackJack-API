package com.Ulises.BlackJackAPI.service;

import com.Ulises.BlackJackAPI.domain.entity.CardEntity;
import com.Ulises.BlackJackAPI.domain.entity.GameEntity;
import com.Ulises.BlackJackAPI.domain.entity.HandEntity;
import com.Ulises.BlackJackAPI.domain.enums.HandType;
import com.Ulises.BlackJackAPI.domain.factory.CardFactory;
import com.Ulises.BlackJackAPI.domain.services.ScoreCalculator;
import com.Ulises.BlackJackAPI.dto.CardResponse;
import com.Ulises.BlackJackAPI.dto.HandResponse;
import com.Ulises.BlackJackAPI.exception.InvalidMoveException;
import com.Ulises.BlackJackAPI.repository.CardRepository;
import com.Ulises.BlackJackAPI.repository.HandRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class HandService {

    private final HandRepository handRepository;
    private final CardRepository cardRepository;
    private final DeckService deckService;
    private final CardFactory cardFactory;
    private final ScoreCalculator scoreCalculator;

    public HandService(
            HandRepository handRepository,
            CardRepository cardRepository,
            DeckService deckService,
            CardFactory cardFactory,
            ScoreCalculator scoreCalculator) {
        this.handRepository = handRepository;
        this.cardRepository = cardRepository;
        this.deckService = deckService;
        this.cardFactory = cardFactory;
        this.scoreCalculator = scoreCalculator;
    }

    public Mono<HandEntity> createPlayerAndCroupierHands(GameEntity game) {
        HandEntity playerHand = new HandEntity(game.getId(), HandType.PLAYER, 0);
        HandEntity croupierHand = new HandEntity(game.getId(), HandType.CROUPIER, 0);
        return handRepository.save(playerHand)
                .zipWith(handRepository.save(croupierHand))
                .map(tuple -> tuple.getT1());
    }

    public Mono<HandEntity> getPlayerHand(Long gameId, int handIndex) {
        return handRepository.findByGameIdAndTypeAndHandIndex(gameId, HandType.PLAYER, handIndex)
                .switchIfEmpty(Mono.error(new InvalidMoveException("Hand not found")));
    }

    public Mono<HandEntity> getCroupierHand(Long gameId) {
        return handRepository.findByGameIdAndTypeAndHandIndex(gameId, HandType.CROUPIER, 0);
    }

    public Mono<List<HandEntity>> getAllHands(Long gameId) {
        return handRepository.findByGameId(gameId).collectList();
    }

    public Mono<HandEntity> hit(HandEntity hand, Long gameId) {
        return deckService.drawCard(gameId)
                .flatMap(card -> {
                    CardEntity cardEntity = cardFactory.createCardEntity(hand, card.suit(), card.rank(), false);
                    return cardRepository.save(cardEntity)
                            .flatMap(saved -> updateHandScore(hand));
                });
    }

    public Mono<HandEntity> addCardToHand(HandEntity hand, Long gameId) {
        return deckService.drawCard(gameId)
                .flatMap(card -> {
                    CardEntity cardEntity = cardFactory.createCardEntity(hand, card.suit(), card.rank(), false);
                    return cardRepository.save(cardEntity)
                            .flatMap(saved -> updateHandScore(hand));
                });
    }

    public Mono<HandEntity> updateHandScore(HandEntity hand) {
        return cardRepository.findByHandIdOrderById(hand.getId())
                .collectList()
                .flatMap(cards -> {
                    int score = scoreCalculator.calculateHandScore(cards);
                    hand.setScore(score);
                    return handRepository.save(hand);
                });
    }

    public Mono<Boolean> isPlayerBust(Long gameId, int handIndex) {
        return getPlayerHand(gameId, handIndex)
                .map(hand -> hand.getScore() > 21);
    }

    public Mono<List<HandResponse>> buildHandResponses(GameEntity game, boolean revealCroupier) {
        return handRepository.findByGameId(game.getId())
                .flatMap(hand -> cardRepository.findByHandIdOrderById(hand.getId())
                        .collectList()
                        .map(cards -> buildHandResponse(hand, cards, revealCroupier)))
                .collectList();
    }

    private HandResponse buildHandResponse(HandEntity hand, List<CardEntity> cards, boolean revealCroupier) {
        List<CardResponse> cardResponses = cards.stream()
                .map(c -> new CardResponse(c.getSuit(), c.getRank(), c.getValue(), c.getIsHidden()))
                .toList();
        return new HandResponse(hand.getId(), hand.getType(), hand.getHandIndex(), hand.getScore(), cardResponses);
    }
}