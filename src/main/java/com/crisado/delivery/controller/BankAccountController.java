package com.crisado.delivery.controller;

import com.crisado.delivery.model.BankAccount;
import com.crisado.delivery.repository.BankAccountRepository;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/bank-accounts")
@AllArgsConstructor
public class BankAccountController {

    private final BankAccountRepository bankAccountRepository;

    @GetMapping
    public List<BankAccount> getAllBankAccounts() {
        return bankAccountRepository.findAll();
    }


}

