package com.crisado.delivery.mapper;

import com.crisado.delivery.dto.OrderResponse;
import com.crisado.delivery.model.Order;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.spi.MappingContext;
import org.springframework.stereotype.Component;

@Component
public class OrderToOrderResponseConverter implements Converter<Order, OrderResponse> {

    public OrderToOrderResponseConverter(ModelMapper modelMapper) {
        modelMapper.createTypeMap(Order.class, OrderResponse.class)
                .setPostConverter(this);
    }

    @Override
    public OrderResponse convert(MappingContext<Order, OrderResponse> context) {
        Order source = context.getSource();
        OrderResponse destination = context.getDestination();
        if (source.getDelivery() != null) {
            destination.setDelivered(source.getDelivery().isDelivered());
        }
        return destination;
    }

}
