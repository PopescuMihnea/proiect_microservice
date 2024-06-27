package com.example.card.services.implementation;

import com.example.card.model.Transaction;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.constraints.Min;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient(value = "transaction")
public interface TransactionServiceProxy {
    @GetMapping("/api/transaction/getAll/user/{cardId}")
    ResponseEntity<PagedModel<EntityModel<Transaction>>> getAllTransactionsByCard(@RequestHeader(name = "awbd-id", required = false)
                                                                                  String correlationId,
                                                                                  @PathVariable
                                                                                  @Parameter(description = "The id of the card")
                                                                                  @Min(1)
                                                                                  long cardId,
                                                                                  Pageable p);
}
