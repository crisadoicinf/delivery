package com.crisado.delivery.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.crisado.delivery.model.OrderDelivery;

@Repository
public interface OrderDeliveryRepository extends JpaRepository<OrderDelivery, Long> {

}
