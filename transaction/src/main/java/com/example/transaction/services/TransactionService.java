package com.example.transaction.services;

import com.example.transaction.model.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigInteger;
import java.util.Date;

public interface TransactionService {
    Transaction get(Long id);

    Transaction getByCardId(Long id, Long cardId);

    Transaction post(Transaction transaction);

    Transaction put(Transaction transaction);

    boolean delete(Long id);

    Page<Transaction> getAll(Pageable p);

    Page<Transaction> getAllByCardId(Long cardId, Pageable p);

    Page<Transaction> getAllByCardIdAndRecipientName(Long cardId, String recipientName, Pageable p);

    Page<Transaction> getAllByCardIdAndAmountBetween(Long cardId, BigInteger amountMin, BigInteger amountMax, Pageable p);

    Page<Transaction> getAllByCardIdAndTransactionDateBefore(Long cardId, Date maxTransactionDate, Pageable p);
}
