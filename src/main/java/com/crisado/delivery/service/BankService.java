package com.crisado.delivery.service;

import static java.util.stream.Collectors.toList;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.crisado.delivery.dto.BankAccountDto;
import com.crisado.delivery.repository.BankAccountRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class BankService {

    private final BankAccountRepository bankAccountRepository;
    private final ModelMapper mapper;

    public List<BankAccountDto> getAllBankAccounts() {
        return bankAccountRepository.findAll()
                .stream()
                .map(bank -> mapper.map(bank, BankAccountDto.class))
                .collect(toList());
    }
}
