package com.crisado.delivery.mapper;

import com.crisado.delivery.dto.OrderDto;
import com.crisado.delivery.model.Order;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.spi.MappingContext;
import org.springframework.stereotype.Component;

@Component
public class OrderToOrderResponseConverter implements Converter<Order, OrderDto> {

    public OrderToOrderResponseConverter(ModelMapper modelMapper) {
        modelMapper.createTypeMap(Order.class, OrderDto.class)
                .setPostConverter(this);
    }

    @Override
    public OrderDto convert(MappingContext<Order, OrderDto> context) {
        Order source = context.getSource();
        OrderDto destination = context.getDestination();
        if (source.getDelivery() != null) {
            destination.setDelivered(source.getDelivery().isDelivered());
        }
        return destination;
    }

}
