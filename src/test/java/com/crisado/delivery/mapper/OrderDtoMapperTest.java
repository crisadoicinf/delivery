package com.crisado.delivery.mapper;

import com.crisado.delivery.config.MapperConfig;
import com.crisado.delivery.dto.OrderDto;
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
import static org.assertj.core.groups.Tuple.tuple;

@ExtendWith(MockitoExtension.class)
class OrderDtoMapperTest {

    private final ModelMapper mapper = new MapperConfig().getModelMapper();

    @Test
    void mapOrder() {
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

        var orderDto = mapper.map(order, OrderDto.class);
        assertThat(orderDto)
                .extracting(
                        OrderDto::getId,
                        OrderDto::getCustomerName,
                        OrderDto::getCustomerPhone,
                        OrderDto::getDeliveryAddress,
                        OrderDto::getDeliveryDate,
                        OrderDto::getDeliveryRiderId,
                        OrderDto::isDelivered,
                        OrderDto::isPaid,
                        OrderDto::getItemsTotalPrice,
                        OrderDto::getDeliveryPrice,
                        OrderDto::getDiscount,
                        OrderDto::getTotalPrice,
                        OrderDto::getPaymentsTotalAmount
                )
                .containsExactly(
                        1L,
                        "customerName",
                        "0812345678",
                        "123 street",
                        deliveryDate,
                        1,
                        true,
                        true,
                        40D,
                        1.5D,
                        2D,
                        39.5D,
                        39.5D
                );
        assertThat(orderDto.getItems())
                .extracting(
                        OrderDto.OrderItem::getId,
                        OrderDto.OrderItem::getProductId,
                        OrderDto.OrderItem::getPosition,
                        OrderDto.OrderItem::getQuantity,
                        OrderDto.OrderItem::getUnitPrice,
                        OrderDto.OrderItem::getTotalPrice,
                        OrderDto.OrderItem::getNote
                )
                .containsExactlyInAnyOrder(
                        tuple(
                                1L,
                                product1.getId(),
                                0,
                                2,
                                product1.getPrice(),
                                2 * product1.getPrice(),
                                "note1"
                        ),
                        tuple(
                                2L,
                                product2.getId(),
                                1,
                                1,
                                product2.getPrice(),
                                1 * product2.getPrice(),
                                "note2"
                        )
                );

    }

}
