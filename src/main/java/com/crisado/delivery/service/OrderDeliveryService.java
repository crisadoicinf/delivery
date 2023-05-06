package com.crisado.delivery.service;

import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.crisado.delivery.model.Order;
import com.crisado.delivery.model.OrderDelivery;
import com.crisado.delivery.repository.OrderDeliveryRepository;
import com.crisado.delivery.repository.OrderRepository;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service
@Transactional
@AllArgsConstructor
public class OrderDeliveryService {

	private final OrderService orderService;
	private final OrderRepository orderRepository;
	private final OrderDeliveryRepository deliveryRepository;

	public List<Order> getOrders(ZonedDateTime date, int riderId) {
		ZonedDateTime to = date.plusDays(1);
		return orderRepository.findAllByDeliveryDateAndRiderId(date, to, riderId);
	}

	public void markOrderDelivered(long orderId, boolean delivered) {
		OrderDelivery delivery = orderService.getOrder(orderId).getDelivery();
		delivery.setDelivered(delivered);
		deliveryRepository.save(delivery);
	}

}
