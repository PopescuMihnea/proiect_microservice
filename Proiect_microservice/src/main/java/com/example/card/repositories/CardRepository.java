package com.example.card.repositories;

import com.example.card.model.Card;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigInteger;

public interface CardRepository extends JpaRepository<Card, Long> {

    boolean deleteCardById(Long id);

    Page<Card> findCardsByUserId(Long userId, Pageable pageable);

    Page<Card> findCardsByBankContainsIgnoreCase(String bank, Pageable pageable);

    Page<Card> findCardsByAmountGreaterThanEqual(BigInteger amount, Pageable pageable);

    Page<Card> findCardsByNameContainsIgnoreCase(String name, Pageable pageable);
}
