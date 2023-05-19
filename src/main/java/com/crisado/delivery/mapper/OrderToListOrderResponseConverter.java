package com.crisado.delivery.mapper;

import com.crisado.delivery.dto.OrderSummaryDto;
import com.crisado.delivery.model.Order;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.spi.MappingContext;
import org.springframework.stereotype.Component;

@Component
public class OrderToListOrderResponseConverter implements Converter<Order, OrderSummaryDto> {

    public OrderToListOrderResponseConverter(ModelMapper modelMapper) {
        modelMapper.createTypeMap(Order.class, OrderSummaryDto.class)
                .setPostConverter(this);
    }

    @Override
    public OrderSummaryDto convert(MappingContext<Order, OrderSummaryDto> context) {
        Order source = context.getSource();
        OrderSummaryDto destination = context.getDestination();
        destination.setTotalItems(source.getItems().size());
        if (source.getDelivery() != null) {
            destination.setDelivered(source.getDelivery().isDelivered());
        }
        return destination;
    }

}
