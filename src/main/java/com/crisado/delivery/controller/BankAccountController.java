package com.crisado.delivery.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.crisado.delivery.dto.BankAccountDto;
import com.crisado.delivery.service.BankService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/bank-accounts")
@AllArgsConstructor
public class BankAccountController {

    private final BankService bankService;

    @GetMapping
    public ResponseEntity<List<BankAccountDto>> getAllBankAccounts() {
        var bankAccounts = bankService.getAllBankAccounts();
        return ResponseEntity.ok(bankAccounts);
    }

}
