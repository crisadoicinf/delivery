package com.crisado.delivery.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import com.crisado.delivery.dto.BankAccountDto;
import com.crisado.delivery.model.BankAccount;
import com.crisado.delivery.repository.BankAccountRepository;

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
        var bankDto1 = new BankAccountDto();
        var bankDto2 = new BankAccountDto();

        when(bankAccountRepository.findAll())
                .thenReturn(List.of(new BankAccount(), new BankAccount()));
        when(mapper.map(any(BankAccount.class), eq(BankAccountDto.class)))
                .thenReturn(bankDto1,bankDto2);

        assertThat(bankService.getAllBankAccounts())
                .containsExactly(bankDto1, bankDto2);
    }

}
