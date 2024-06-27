package com.example.transaction.services.implementation;

import com.example.transaction.error.TransactionNotFoundError;
import com.example.transaction.model.Transaction;
import com.example.transaction.repositories.TransactionRepository;
import com.example.transaction.services.TransactionService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.Date;

@Service
@RequiredArgsConstructor
@Transactional
public class TransactionServiceImpl implements TransactionService {
    private final TransactionRepository transactionRepository;

    @Override
    public Transaction get(Long id) {
        return transactionRepository.findById(id).orElseThrow(() -> new TransactionNotFoundError("Transaction not found with id: " + id));
    }

    @Override
    public Transaction getByCardId(Long id, Long cardId) {
        return transactionRepository.getTransactionByIdAndCardId(id, cardId).orElseThrow(() -> new TransactionNotFoundError("Transaction not found with cardId: " + cardId));
    }

    @Override
    public Transaction post(Transaction transaction) {
        transaction.setId(null);
        return transactionRepository.save(transaction);
    }

    @Override
    public Transaction put(Transaction transaction) {
        var existingTransaction = transactionRepository.existsById(transaction.getId());

        if (!existingTransaction)
        {
            throw new TransactionNotFoundError("The transaction you want to update does not exist");
        }

        return transactionRepository.save(transaction);
    }

    @Override
    public boolean delete(Long id) {
        return transactionRepository.deleteTransactionById(id);
    }

    @Override
    public Page<Transaction> getAll(Pageable p) {
        return transactionRepository.findAll(p);
    }

    @Override
    public Page<Transaction> getAllByCardId(Long cardId, Pageable p) {
        return transactionRepository.findTransactionsByCardId(cardId, p);
    }

    @Override
    public Page<Transaction> getAllByCardIdAndRecipientName(Long cardId, String recipientName, Pageable p) {
        return transactionRepository.findTransactionsByCardIdAndRecipientNameContainingIgnoreCase(cardId, recipientName, p);
    }

    @Override
    public Page<Transaction> getAllByCardIdAndAmountBetween(Long cardId, BigInteger minAmount, BigInteger maxAmount, Pageable p) {
        return transactionRepository.findTransactionsByCardIdAndAmountBetween(cardId, minAmount, maxAmount, p);
    }

    @Override
    public Page<Transaction> getAllByCardIdAndTransactionDateBefore(Long cardId, Date maxTransactionDate, Pageable p) {
        return transactionRepository.findTransactionsByCardIdAndTransactionDateBefore(cardId, maxTransactionDate, p);
    }
}
