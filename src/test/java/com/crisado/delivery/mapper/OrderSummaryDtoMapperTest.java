package com.crisado.delivery.mapper;

import com.crisado.delivery.config.MapperConfig;
import com.crisado.delivery.dto.OrderSummaryDto;
import com.crisado.delivery.model.CashPayment;
import com.crisado.delivery.model.Order;
import com.crisado.delivery.model.OrderDelivery;
import com.crisado.delivery.model.OrderItem;
import com.crisado.delivery.model.Product;
import com.crisado.delivery.model.Rider;
import com.crisado.delivery.model.TransferencePayment;
import java.time.ZonedDateTime;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class OrderSummaryDtoMapperTest {

    private final ModelMapper mapper = new MapperConfig().getModelMapper();

    @Test
    public void mapOrder() {
        var deliveryDate = ZonedDateTime.now();
        var product1 = Product.builder()
                .id(1)
                .name("product1")
                .price(10)
                .build();
        var product2 = Product.builder()
                .id(2)
                .name("product2")
                .price(20)
                .build();
        var order = Order.builder()
                .id(1L)
                .customerName("customerName")
                .customerPhone("0812345678")
                .date(ZonedDateTime.now())
                .discount(2D)
                .delivery(OrderDelivery.builder()
                        .address("123 street")
                        .date(deliveryDate)
                        .price(1.5D)
                        .rider(Rider.builder()
                                .id(1)
                                .name("rider1")
                                .build())
                        .delivered(true)
                        .build())
                .items(Set.of(
                        OrderItem.builder()
                                .id(1L)
                                .note("note1")
                                .position(0)
                                .product(product1)
                                .quantity(2)
                                .unitPrice(product1.getPrice())
                                .totalPrice(2 * product1.getPrice())
                                .build(),
                        OrderItem.builder()
                                .id(2L)
                                .note("note2")
                                .position(1)
                                .product(product2)
                                .quantity(1)
                                .unitPrice(product2.getPrice())
                                .totalPrice(1 * product2.getPrice())
                                .build()
                ))
                .payments(Set.of(
                        CashPayment.builder().amount(20D).build(),
                        TransferencePayment.builder().amount(19.5D).build()
                ))
                .build();

        var orderSummaryDto = mapper.map(order, OrderSummaryDto.class);
        assertThat(orderSummaryDto)
                .extracting(
                        OrderSummaryDto::getId,
                        OrderSummaryDto::getCustomerName,
                        OrderSummaryDto::getCustomerPhone,
                        OrderSummaryDto::isDelivered,
                        OrderSummaryDto::getDeliveryAddress,
                        OrderSummaryDto::getDeliveryDate,
                        OrderSummaryDto::getTotalPrice,
                        OrderSummaryDto::getTotalItems,
                        OrderSummaryDto::getDeliveryRiderName,
                        OrderSummaryDto::isPaid
                )
                .containsExactly(
                        1L,
                        "customerName",
                        "0812345678",
                        true,
                        "123 street",
                        deliveryDate,
                        39.5D,
                        3,
                        "rider1",
                        true
                );
    }

}
