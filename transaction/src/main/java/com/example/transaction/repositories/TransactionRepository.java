package com.example.transaction.repositories;

import com.example.transaction.model.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigInteger;
import java.util.Date;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Optional<Transaction> getTransactionByIdAndCardId(Long id, Long cardId);

    boolean deleteTransactionById(Long id);

    Page<Transaction> findTransactionsByCardId(Long cardId, Pageable pageable);

    Page<Transaction> findTransactionsByCardIdAndRecipientNameContainingIgnoreCase(Long cardId, String recipientName, Pageable pageable);

    Page<Transaction> findTransactionsByCardIdAndAmountBetween(Long cardId, BigInteger min, BigInteger max, Pageable pageable);

    Page<Transaction> findTransactionsByCardIdAndTransactionDateBefore(Long cardId, Date maxDate, Pageable pageable);

}
