package com.example.card.services;

import com.example.card.model.Card;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigInteger;

public interface CardService {
    Card get(Long id);

    Card post(Card card);

    Card put(Card card);

    boolean delete(Long id);

    Page<Card> getAll(Pageable p);

    Page<Card> getAllByUser(Long userId, Pageable p);

    Page<Card> getAllByBank(String bank, Pageable p);

    Page<Card> getAllByAmountGreater(BigInteger amount, Pageable p);

    Page<Card> getAllByName(String name, Pageable p);
}
