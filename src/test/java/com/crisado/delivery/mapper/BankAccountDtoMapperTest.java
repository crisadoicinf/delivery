package com.crisado.delivery.mapper;

import com.crisado.delivery.config.MapperConfig;
import com.crisado.delivery.dto.BankAccountDto;
import com.crisado.delivery.model.BankAccount;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class BankAccountDtoMapperTest {

    private final ModelMapper mapper = new MapperConfig().getModelMapper();

    @Test
    public void mapBankAccount() {
        var bankAccount = BankAccount.builder()
                .id(1)
                .name("bank1")
                .owner("owner1")
                .build();

        assertThat(mapper.map(bankAccount, BankAccountDto.class))
                .extracting(BankAccountDto::getId, BankAccountDto::getName, BankAccountDto::getOwner)
                .containsExactly(1, "bank1", "owner1");
    }

}
