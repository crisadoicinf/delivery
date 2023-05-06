package com.crisado.delivery.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.crisado.delivery.dto.CashPaymentList;
import com.crisado.delivery.dto.PaymentList;
import com.crisado.delivery.dto.TransferencePaymentList;
import com.crisado.delivery.model.CashPayment;
import com.crisado.delivery.model.TransferencePayment;

@Configuration
public class MapperConfig {

    @Bean
    public ModelMapper getModelMapper() {
        var mapper = new ModelMapper();
        mapper.createTypeMap(CashPayment.class, PaymentList.class)
                .setConverter(context -> mapper.map(context.getSource(), CashPaymentList.class));
        mapper.createTypeMap(TransferencePayment.class, PaymentList.class)
                .setConverter(context -> mapper.map(context.getSource(), TransferencePaymentList.class));
        return mapper;
    }
    
}
