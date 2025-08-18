package com.example.lms.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.example.lms.dto.credit.CreditRequestDTO;
import com.example.lms.dto.debit.DebitRequestDTO;
import com.example.lms.dto.transaction.TransactionResponseDTO;

@FeignClient(name = "card-service", url = "http://localhost:8080/transactions") // TODO: Organize ports accross all three microservices.
public interface CardClient {

    @PostMapping("/debit")
    ResponseEntity<TransactionResponseDTO> debit(@RequestBody DebitRequestDTO request);

    @PostMapping("/credit")
    ResponseEntity<TransactionResponseDTO> credit(@RequestBody CreditRequestDTO request);
}

