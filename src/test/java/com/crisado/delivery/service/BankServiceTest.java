package com.crisado.delivery.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import com.crisado.delivery.dto.BankAccountDto;
import com.crisado.delivery.model.BankAccount;
import com.crisado.delivery.repository.BankAccountRepository;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

@ExtendWith(MockitoExtension.class)
public class BankServiceTest {

    @Mock
    private BankAccountRepository bankAccountRepository;
    @Mock
    private ModelMapper mapper;
    @InjectMocks
    private BankService bankService;

    @Test
    void getAllBankAccounts() {
        var bank1 = BankAccount.builder().build();
        var bank2 = BankAccount.builder().build();

        when(bankAccountRepository.findAll())
                .thenReturn(List.of(bank1, bank2));
        when(mapper.map(eq(bank1), eq(BankAccountDto.class)))
                .thenReturn(BankAccountDto.builder()
                        .id(1)
                        .name("bank1")
                        .owner("owner1")
                        .build());
        when(mapper.map(eq(bank2), eq(BankAccountDto.class)))
                .thenReturn(BankAccountDto.builder()
                        .id(2)
                        .name("bank2")
                        .owner("owner2")
                        .build());

        assertThat(bankService.getAllBankAccounts())
                .extracting(BankAccountDto::getId, BankAccountDto::getName, BankAccountDto::getOwner)
                .containsExactly(
                        tuple(1, "bank1", "owner1"),
                        tuple(2, "bank2", "owner2"));
    }

}
