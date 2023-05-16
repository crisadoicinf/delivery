package com.crisado.delivery.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.crisado.delivery.dto.CashPaymentDto;
import com.crisado.delivery.dto.PaymentDto;
import com.crisado.delivery.dto.TransferencePaymentDto;
import com.crisado.delivery.model.CashPayment;
import com.crisado.delivery.model.TransferencePayment;

@Configuration
public class MapperConfig {

    @Bean
    public ModelMapper getModelMapper() {
        var mapper = new ModelMapper();
        mapper.createTypeMap(CashPayment.class, PaymentDto.class)
                .setConverter(context -> mapper.map(context.getSource(), CashPaymentDto.class));
        mapper.createTypeMap(TransferencePayment.class, PaymentDto.class)
                .setConverter(context -> mapper.map(context.getSource(), TransferencePaymentDto.class));
        return mapper;
    }
    
}
