package com.example.card.controllers;


import com.example.card.configuration.PropertiesConfiguration;
import com.example.card.model.Card;
import com.example.card.services.CardService;
import com.example.card.services.implementation.TransactionServiceProxy;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/card")
@RequiredArgsConstructor
@Validated
@Slf4j
public class CardController {
    private final CardService cardService;
    private final PropertiesConfiguration propertiesConfiguration;
    private final TransactionServiceProxy transactionServiceProxy;

    private void addCardLinks(Card card) {
        Link selfLink = linkTo(methodOn(CardController.class).getCard(null, card.getId())).withSelfRel();
        card.add(selfLink);

        Link postLink = linkTo(methodOn(CardController.class).createCard(null, card)).withRel("createCard");
        card.add(postLink);

        Link putLink = linkTo(methodOn(CardController.class).modifyCard(null, card)).withRel("modifyCard");
        card.add(putLink);

        Link deleteLink = linkTo(methodOn(CardController.class).deleteCard(null, card.getId())).withRel("deleteCard");
        card.add(deleteLink);
    }

    @Operation(summary = "Gets the card with the given id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Successfully found the card with the given id",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Card.class))}),
    })
    @GetMapping("/get/{id}")
    @CircuitBreaker(name="getTransactionsByCard", fallbackMethod = "getCardFallback")
    public ResponseEntity<Card> getCard(@RequestHeader(value = "awbd-id", required = false)
                                        String correlationId,
                                        @PathVariable
                                        @Parameter(description = "The id of the card you want to get information for")
                                        @Min(1)
                                        long id) {

        log.info("correlation-id get card: {}", correlationId);

        var card = cardService.get(id);

        addCardLinks(card);

        if(propertiesConfiguration.isShowTransactions())
        {
            var transactions = transactionServiceProxy.getAllTransactionsByCard(correlationId, card.getId(), PageRequest.of(0, 10));
            card.setTransactions(transactions.getBody());
        }

        return new ResponseEntity<>(card, HttpStatus.OK);
    }

    @Operation(summary = "Creates a new card")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201",
                    description = "Successfully created card with the given data",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Card.class))}),
    })
    @PostMapping("/create")
    public ResponseEntity<Card> createCard(@RequestHeader(name = "awbd-id", required = false)
                                           String correlationId,
                                           @Valid
                                              @io.swagger.v3.oas.annotations.parameters.
                                                  RequestBody(description = "The data of the card that is to be created")
                                           @RequestBody
                                           Card card) {
        log.info("correlation-id create card: {}", correlationId);

        var postedCard = cardService.post(card);

        addCardLinks(postedCard);

        return new ResponseEntity<>(postedCard, HttpStatus.CREATED);
    }

    @Operation(summary = "Modifies a card with the given data")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Successfully modified card with the given data",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Card.class))}),
    })
    @PutMapping("/modify")
    public ResponseEntity<Card> modifyCard(@RequestHeader(name = "awbd-id", required = false)
                                           String correlationId,
                                           @Valid
                                              @io.swagger.v3.oas.annotations.parameters.
                                                  RequestBody(description = "The data to modify the card with")
                                           @RequestBody
                                           Card card) {
        log.info("correlation-id modify card: {}", correlationId);

        var modifiedCard = cardService.put(card);

        addCardLinks(modifiedCard);

        return new ResponseEntity<>(modifiedCard, HttpStatus.OK);
    }

    @Operation(summary = "Deletes the card with the given id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "The card has been deleted",
                    content = {@Content(mediaType = MediaType.TEXT_PLAIN_VALUE,
                            schema = @Schema(implementation = String.class),
                            examples = {@ExampleObject(value = "Card has been deleted")})}),
            @ApiResponse(responseCode = "400",
                    description = "Card deletion failed due to wrong id or database error",
                    content = {@Content(mediaType = MediaType.TEXT_PLAIN_VALUE,
                            schema = @Schema(implementation = String.class),
                            examples = {@ExampleObject(value = "Card deletion failed")})}),
    })
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteCard(@RequestHeader(name = "awbd-id", required = false)
                                             String correlationId,
                                             @PathVariable
                                             @Parameter(description = "The id of the card to delete")
                                             @Min(1)
                                             long id) {

        log.info("correlation-id delete card: {}", correlationId);

        var result = cardService.delete(id);
        return new ResponseEntity<>(
                result ? "Card has been deleted" : "Card deletion failed",
                result ? HttpStatus.OK : HttpStatus.BAD_REQUEST
        );
    }

    @Operation(summary = "Gets a list of all cards")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Successfully queried cards"),
    })
    @GetMapping("/getAll")
    public ResponseEntity<PagedModel<EntityModel<Card>>> getAllCards(@RequestHeader(name = "awbd-id", required = false)
                                                                     String correlationId,
                                                                     Pageable p,
                                                                     PagedResourcesAssembler<Card> assembler)
    {
        log.info("correlation-id get all cards: {}", correlationId);
        //log.info(String.valueOf(propertiesConfiguration.isShowTransactions()));

        var cards = cardService.getAll(p);

        for(final Card card : cards)
        {
            addCardLinks(card);
        }

        return new ResponseEntity<>(assembler.toModel(cards), HttpStatus.OK);
    }

    @Operation(summary = "Gets a list of all cards with the given user id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Successfully queried cards"),
    })
    @GetMapping("/getAll/user/{userId}")
    public ResponseEntity<PagedModel<EntityModel<Card>>> getAllCardsByUser(@RequestHeader(name = "awbd-id", required = false)
                                                                           String correlationId,
                                                                           @PathVariable
                                                                           @Parameter(description = "The id of the user")
                                                                           @Min(1)
                                                                           long userId,
                                                                           Pageable p,
                                                                           PagedResourcesAssembler<Card> assembler)
    {
        log.info("correlation-id get all cards by user: {}", correlationId);

        var cards = cardService.getAllByUser(userId, p);

        for(final Card card : cards)
        {
            addCardLinks(card);
        }

        return new ResponseEntity<>(assembler.toModel(cards), HttpStatus.OK);
    }

    @Operation(summary = "Gets a list of all cards of the given bank")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Successfully queried cards"),
    })
    @GetMapping("/getAll/bank/{bank}")
    public ResponseEntity<PagedModel<EntityModel<Card>>> getAllCardsByBank(@RequestHeader(name = "awbd-id", required = false)
                                                                           String correlationId,
                                                                           @PathVariable
                                                                           @Parameter(description = "The name of the bank")
                                                                           @NotBlank
                                                                           String bank,
                                                                           Pageable p,
                                                                           PagedResourcesAssembler<Card> assembler)
    {
        log.info("correlation-id get all cards by bank: {}", correlationId);

        var cards = cardService.getAllByBank(bank, p);

        for(final Card card : cards)
        {
            addCardLinks(card);
        }

        return new ResponseEntity<>(assembler.toModel(cards), HttpStatus.OK);
    }

    @Operation(summary = "Gets a list of all cards that have at least the amount specified of money")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Successfully queried cards"),
    })
    @GetMapping("/getAll/amount")
    public ResponseEntity<PagedModel<EntityModel<Card>>> getAllCardsWithAmountGreater(@RequestHeader(name = "awbd-id", required = false)
                                                                                      String correlationId,
                                                                                      @RequestParam
                                                                                      @Parameter(description = "The minimum amount")
                                                                                      @NotNull
                                                                                      @Min(0) BigInteger amount,
                                                                                      Pageable p,
                                                                                      PagedResourcesAssembler<Card> assembler)
    {
        log.info("correlation-id get all cards with max amount: {}", correlationId);

        var cards = cardService.getAllByAmountGreater(amount, p);

        for(final Card card : cards)
        {
            addCardLinks(card);
        }

        return new ResponseEntity<>(assembler.toModel(cards), HttpStatus.OK);
    }

    @Operation(summary = "Gets a list of all cards that have the given name")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Successfully queried cards"),
    })
    @GetMapping("/getAll/name/{name}")
    public ResponseEntity<PagedModel<EntityModel<Card>>> getAllCardsByName(@RequestHeader(name = "awbd-id", required = false)
                                                                           String correlationId,
                                                                           @PathVariable
                                                                           @Parameter(description = "The name of the card")
                                                                           @NotBlank
                                                                           String name,
                                                                           Pageable p,
                                                                           PagedResourcesAssembler<Card> assembler)
    {
        log.info("correlation-id get all cards by name: {}", correlationId);

        var cards = cardService.getAllByName(name, p);

        for(final Card card : cards)
        {
            addCardLinks(card);
        }

        return new ResponseEntity<>(assembler.toModel(cards), HttpStatus.OK);
    }

    private ResponseEntity<Card> getCardFallback(long id, Throwable throwable)
    {
        log.info("Fallback#000000000000000000000000000000#");

        var card = cardService.get(id);

        addCardLinks(card);

        return new ResponseEntity<>(card, HttpStatus.OK);
    }
}
