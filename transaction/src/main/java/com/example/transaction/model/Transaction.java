package com.example.transaction.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;

import java.math.BigInteger;
import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "Transaction")
public class Transaction extends RepresentationModel<Transaction> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_id")
    private Long id;

    @Column(name = "card_id")
    @NotNull
    @Min(value = 1)
    private Long cardId;

    @NotBlank(message = "Must have a recipient name")
    @Size(min = 5,
            message = "Enter a more descriptive(longer) recipient name")
    @Size(max = 100, message = "The recipient name is too long")
    private String recipientName;

    @Past(message = "Transaction cannot be made in the future")
    @NotNull
    private Date transactionDate;

    @NotNull(message = "Must enter transaction amount")
    private BigInteger amount;
}
