package com.crisado.delivery.service;


import java.util.List;

import org.springframework.stereotype.Service;

import com.crisado.delivery.dto.BankAccountDto;
import com.crisado.delivery.repository.BankAccountRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class BankAccountService {

    private final Services services;
    private final BankAccountRepository bankAccountRepository;

    public List<BankAccountDto> getBankAccounts() {
        return bankAccountRepository.findAll()
                .stream()
                .map(bank -> services.map(bank, BankAccountDto.class))
                .toList();
    }
    
}
