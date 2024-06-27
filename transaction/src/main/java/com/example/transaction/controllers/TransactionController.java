package com.example.transaction.controllers;

import com.example.transaction.configuration.PropertiesConfiguration;
import com.example.transaction.model.Transaction;
import com.example.transaction.services.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
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
import java.util.Date;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/transaction")
@RequiredArgsConstructor
@Validated
@Slf4j
public class TransactionController {
    private final TransactionService transactionService;
    private final PropertiesConfiguration propertiesConfiguration;

    private void addTransactionLinks(Transaction transaction) {
        Link selfLink = linkTo(methodOn(TransactionController.class).getTransactionWithCardId(null, transaction.getId(), transaction.getCardId())).withSelfRel();
        transaction.add(selfLink);

        Link postLink = linkTo(methodOn(TransactionController.class).createTransaction(null, transaction)).withRel("createTransaction");
        transaction.add(postLink);

        Link putLink = linkTo(methodOn(TransactionController.class).modifyTransaction(null, transaction)).withRel("modifyTransaction");
        transaction.add(putLink);

        Link deleteLink = linkTo(methodOn(TransactionController.class).deleteTransaction(null, transaction.getId())).withRel("deleteTransaction");
        transaction.add(deleteLink);
    }

    @Operation(summary = "Gets the transaction with the given id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Successfully found the transaction with the given id",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Transaction.class))}),
    })
    @GetMapping("/get/{id}")
    public ResponseEntity<Transaction> getTransaction(@RequestHeader(value = "awbd-id", required = false)
                                                      String correlationId,
                                                      @PathVariable
                                                      @Parameter(description = "The id of the transaction you want to get information for")
                                                      @Min(1)
                                                      long id) {

        log.info("correlation-id get transaction: {}", correlationId);

        var transaction = transactionService.get(id);

        addTransactionLinks(transaction);

        return new ResponseEntity<>(transaction, HttpStatus.OK);
    }

    @Operation(summary = "Gets the transaction with the given id and card it")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Successfully found the transaction with the given id and card it",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Transaction.class))}),
    })
    @GetMapping("/getWithCard/{id}")
    public ResponseEntity<Transaction> getTransactionWithCardId(@RequestHeader(value = "awbd-id", required = false)
                                                                String correlationId,
                                                                @PathVariable
                                                                @Parameter(description = "The id of the transaction you want to get information for")
                                                                @Min(1)
                                                                long id,
                                                                @RequestParam
                                                                @Parameter(description = "The id of the card that the transaction needs to have")
                                                                @NotNull
                                                                @Min(1)
                                                                long cardId) {

        log.info("correlation-id get transaction with card id: {}", correlationId);

        var transaction = transactionService.getByCardId(id, cardId);

        addTransactionLinks(transaction);

        return new ResponseEntity<>(transaction, HttpStatus.OK);
    }

    @Operation(summary = "Creates a new transaction")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201",
                    description = "Successfully created transaction with the given data",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Transaction.class))}),
    })
    @PostMapping("/create")
    public ResponseEntity<Transaction> createTransaction(@RequestHeader(value = "awbd-id", required = false)
                                                         String correlationId,
                                                         @Valid
                                                            @io.swagger.v3.oas.annotations.parameters.
                                                                RequestBody(description = "The data of the transaction that is to be created")
                                                         @RequestBody
                                                         Transaction transaction) {
        log.info("correlation-id create transaction: {}", correlationId);

        var postedTransaction = transactionService.post(transaction);

        addTransactionLinks(postedTransaction);

        return new ResponseEntity<>(postedTransaction, HttpStatus.CREATED);
    }

    @Operation(summary = "Modifies a transaction with the given data")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Successfully modified transaction with the given data",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Transaction.class))}),
    })
    @PutMapping("/modify")
    public ResponseEntity<Transaction> modifyTransaction(@RequestHeader(value = "awbd-id", required = false)
                                                         String correlationId,
                                                         @Valid
                                                         @io.swagger.v3.oas.annotations.parameters.
                                                             RequestBody(description = "The data to modify the transaction with")
                                                         @RequestBody
                                                         Transaction transaction) {
        log.info("correlation-id modify transaction: {}", correlationId);

        var modifiedTransaction = transactionService.put(transaction);

        addTransactionLinks(modifiedTransaction);

        return new ResponseEntity<>(modifiedTransaction, HttpStatus.OK);
    }

    @Operation(summary = "Deletes the transaction with the given id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "The transaction has been deleted",
                    content = {@Content(mediaType = MediaType.TEXT_PLAIN_VALUE,
                            schema = @Schema(implementation = String.class),
                            examples = {@ExampleObject(value = "Transaction has been deleted")})}),
            @ApiResponse(responseCode = "400",
                    description = "Transaction deletion failed due to wrong id or database error",
                    content = {@Content(mediaType = MediaType.TEXT_PLAIN_VALUE,
                            schema = @Schema(implementation = String.class),
                            examples = {@ExampleObject(value = "Transaction deletion failed")})}),
    })
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteTransaction(@RequestHeader(value = "awbd-id", required = false)
                                                    String correlationId,
                                                    @PathVariable
                                                    @Parameter(description = "The id of the transaction to delete")
                                                    @Min(1)
                                                    long id) {

        log.info("correlation-id delete transaction: {}", correlationId);

        var result = transactionService.delete(id);
        return new ResponseEntity<>(
                result ? "Transaction has been deleted" : "Transaction deletion failed",
                result ? HttpStatus.OK : HttpStatus.BAD_REQUEST
        );
    }

    @Operation(summary = "Gets a list of all transactions")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Successfully queried transactions",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = Transaction.class)))}),
    })
    @GetMapping("/getAll")
    public ResponseEntity<PagedModel<EntityModel<Transaction>>> getAllTransactions(@RequestHeader(value = "awbd-id", required = false)
                                                                                   String correlationId,
                                                                                   Pageable p,
                                                                                   PagedResourcesAssembler<Transaction> assembler)
    {
        log.info("correlation-id get all transactions: {}", correlationId);
        //log.info(String.valueOf(propertiesConfiguration.isConvertToUsd()));

        var transactions = transactionService.getAll(p);

        for(final Transaction transaction : transactions)
        {
            addTransactionLinks(transaction);
        }

        return new ResponseEntity<>(assembler.toModel(transactions), HttpStatus.OK);
    }

    @Operation(summary = "Gets a list of all transactions with the given card id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Successfully queried transactions",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = Transaction.class)))}),
    })
    @GetMapping("/getAll/user/{cardId}")
    public ResponseEntity<PagedModel<EntityModel<Transaction>>> getAllTransactionsByCard(@RequestHeader(value = "awbd-id", required = false)
                                                                                         String correlationId,
                                                                                         @PathVariable
                                                                                         @Parameter(description = "The id of the card")
                                                                                         @Min(1)
                                                                                         long cardId,
                                                                                         Pageable p,
                                                                                         PagedResourcesAssembler<Transaction> assembler)
    {
        log.info("correlation-id get all transactions by card: {}", correlationId);

        var transactions = transactionService.getAllByCardId(cardId, p);

        for(final Transaction transaction : transactions)
        {
            addTransactionLinks(transaction);
        }

        return new ResponseEntity<>(assembler.toModel(transactions), HttpStatus.OK);
    }

    @Operation(summary = "Gets a list of all transactions of the given card id with the given recipient name")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Successfully queried transactions",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = Transaction.class)))}),
    })
    @GetMapping("/getAll/recipient/{cardId}")
    public ResponseEntity<PagedModel<EntityModel<Transaction>>> getAllTransactionsByCardAndRecipient(@RequestHeader(value = "awbd-id", required = false)
                                                                                                     String correlationId,
                                                                                                     @PathVariable
                                                                                                     @Parameter(description = "The id of the card")
                                                                                                     @Min(1)
                                                                                                     long cardId,
                                                                                                     @RequestParam
                                                                                                     @Parameter(description = "The id of the card that the transaction needs to have")
                                                                                                     @NotBlank
                                                                                                     @Size(min = 5, message = "Recipient name too short")
                                                                                                     String recipientName,
                                                                                                     Pageable p,
                                                                                                     PagedResourcesAssembler<Transaction> assembler)
    {
        log.info("correlation-id get all transactions by card and recipient: {}", correlationId);

        var transactions = transactionService.getAllByCardIdAndRecipientName(cardId, recipientName, p);

        for(final Transaction transaction : transactions)
        {
            addTransactionLinks(transaction);
        }

        return new ResponseEntity<>(assembler.toModel(transactions), HttpStatus.OK);
    }

    @Operation(summary = "Gets a list of all transactions that have a given card id and an amount between specified min and max values")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Successfully queried transactions",
                    content = {@Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = Transaction.class)))}),
    })
    @GetMapping("/getAll/amount/{cardId}")
    public ResponseEntity<PagedModel<EntityModel<Transaction>>> getAllTransactionsByCardAndAmount(@RequestHeader(value = "awbd-id", required = false)
                                                                                                  String correlationId,
                                                                                                  @PathVariable
                                                                                                  @Parameter(description = "The id of the card")
                                                                                                  @Min(1)
                                                                                                  long cardId,
                                                                                                  @RequestParam
                                                                                                  @Parameter(description = "The minimum amount of the transaction")
                                                                                                  @NotNull
                                                                                                  @Min(0) BigInteger minAmount,
                                                                                                  @RequestParam
                                                                                                  @Parameter(description = "The maximum amount of the transaction")
                                                                                                  @NotNull
                                                                                                  @Min(0) BigInteger maxAmount,
                                                                                                  Pageable p,
                                                                                                  PagedResourcesAssembler<Transaction> assembler)
    {
        log.info("correlation-id get all transactions by card and amount: {}", correlationId);

        var transactions = transactionService.getAllByCardIdAndAmountBetween(cardId, minAmount, maxAmount, p);

        for(final Transaction transaction : transactions)
        {
            addTransactionLinks(transaction);
        }

        return new ResponseEntity<>(assembler.toModel(transactions), HttpStatus.OK);
    }

    @Operation(summary = "Gets a list of all transactions that have a given card id and have been made before a given date")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Successfully queried transactions"),
    })
    @GetMapping("/getAll/date/{cardId}")
    public ResponseEntity<PagedModel<EntityModel<Transaction>>> getAllTransactionsByCardAndDate(@RequestHeader(value = "awbd-id", required = false)
                                                                                                String correlationId,
                                                                                                @PathVariable
                                                                                                @Parameter(description = "The id of the card")
                                                                                                @Min(1)
                                                                                                long cardId,
                                                                                                @RequestParam
                                                                                                @Parameter(description = "The date which specifies the maximum date of the transaction")
                                                                                                @NotNull
                                                                                                Date maxDate,
                                                                                                Pageable p,
                                                                                                PagedResourcesAssembler<Transaction> assembler)
    {
        log.info("correlation-id get all transactions by card and date: {}", correlationId);

        var transactions = transactionService.getAllByCardIdAndTransactionDateBefore(cardId, maxDate, p);

        for(final Transaction transaction : transactions)
        {
            addTransactionLinks(transaction);
        }

        return new ResponseEntity<>(assembler.toModel(transactions), HttpStatus.OK);
    }
}
