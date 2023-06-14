package com.crisado.delivery.repository;


import com.crisado.delivery.model.OrderDelivery;
import org.springframework.data.repository.Repository;

public interface OrderDeliveryRepository extends Repository<OrderDelivery, Long> {

    OrderDelivery save(OrderDelivery entity);
    
}
