package com.example.card.services.implementation;

import com.example.card.error.CardNotFoundError;
import com.example.card.model.Card;
import com.example.card.repositories.CardRepository;
import com.example.card.services.CardService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigInteger;

@Service
@RequiredArgsConstructor
@Transactional
public class CardServiceImpl implements CardService {

    private final CardRepository cardRepository;

    @Override
    public Card get(Long id) {
        return cardRepository.findById(id).orElseThrow(() -> new CardNotFoundError("Card not found with id: " + id));
    }

    @Override
    public Card post(Card card) {
        card.setId(null);
        return cardRepository.save(card);
    }

    @Override
    public Card put(Card card) {
        var existingCard = cardRepository.existsById(card.getId());

        if (!existingCard)
        {
            throw new CardNotFoundError("The card you want to update does not exist");
        }

        return cardRepository.save(card);
    }

    @Override
    public boolean delete(Long id) {
        return cardRepository.deleteCardById(id);
    }

    @Override
    public Page<Card> getAll(Pageable p) {
        return cardRepository.findAll(p);
    }

    @Override
    public Page<Card> getAllByUser(Long userId, Pageable p) {
        return cardRepository.findCardsByUserId(userId, p);
    }

    @Override
    public Page<Card> getAllByBank(String bank, Pageable p) {
        return cardRepository.findCardsByBankContainsIgnoreCase(bank, p);
    }

    @Override
    public Page<Card> getAllByAmountGreater(BigInteger amount, Pageable p) {
        return cardRepository.findCardsByAmountGreaterThanEqual(amount, p);
    }

    @Override
    public Page<Card> getAllByName(String name, Pageable p) {
        return cardRepository.findCardsByNameContainsIgnoreCase(name, p);
    }
}
