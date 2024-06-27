package com.example.card.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.RepresentationModel;

import java.math.BigInteger;
import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "Card")
public class Card extends RepresentationModel<Card> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "card_id")
    private Long id;

    @Column(name = "user_id")
    @NotNull
    @Min(value = 1)
    private Long userId;

    @NotBlank(message = "Must have a name")
    @Size(min = 5,
            message = "Enter a more descriptive(longer) name")
    @Size(max = 100, message = "The name is too long")
    private String name;

    @NotBlank(message = "Must have a bank")
    @Size(max = 100, message = "The bank name is too long")
    private String bank;

    @NotBlank(message = "Must have a CVV")
    @Pattern(regexp = "^[0-9]{3,4}$", message = "Invalid CVV")
    @Column(unique = true)
    private String CVV;

    @NotBlank(message = "Must have a card number")
    @Pattern(regexp = "^(?:4[0-9]{12}(?:[0-9]{3})?|5[1-5][0-9]{14}|3[47][0-9]{13}|3(?:0[0-5]|[68][0-9])[0-9]{11}|6(?:011|5[0-9]{2})[0-9]{12}|(?:2131|1800|35\\d{3})\\d{11})$",
            message = "Invalid card number")
    @Column(unique = true)
    private String cardNumber;

    @Future(message = "Card has to not be expired")
    @NotNull
    private Date expiryDate;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @Transient
    private PagedModel<EntityModel<Transaction>> transactions;

    // Stored as euro-cents
    private BigInteger amount;
}
