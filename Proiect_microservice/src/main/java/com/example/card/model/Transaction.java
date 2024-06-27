package com.example.card.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;

import java.math.BigInteger;
import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction extends RepresentationModel<Transaction> {
    private Long id;

    private Long cardId;

    private String recipientName;

    private Date transactionDate;

    private BigInteger amount;
}
