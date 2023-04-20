package com.crisado.delivery.mapper;

import com.crisado.delivery.dto.OrderListResponse;
import com.crisado.delivery.model.Order;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.spi.MappingContext;
import org.springframework.stereotype.Component;

@Component
public class OrderToListOrderResponseConverter implements Converter<Order, OrderListResponse> {

    public OrderToListOrderResponseConverter(ModelMapper modelMapper) {
        modelMapper.createTypeMap(Order.class, OrderListResponse.class)
                .setPostConverter(this);
    }

    @Override
    public OrderListResponse convert(MappingContext<Order, OrderListResponse> context) {
        Order source = context.getSource();
        OrderListResponse destination = context.getDestination();
        destination.setTotalItems(source.getItems().size());
        if (source.getDelivery() != null) {
            destination.setDelivered(source.getDelivery().isDelivered());
        }
        return destination;
    }

}
