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

	/**
	 * Retrieves all orders for a specific delivery date and rider ID.
	 * Also includes any orders that do not have a rider assigned.
	 *
	 * @param date    The delivery date to filter by.
	 * @param riderId The ID of the rider to filter by.
	 * 
	 * @return A list of orders.
	 */
	public List<Order> getOrders(ZonedDateTime date, int riderId) {
		ZonedDateTime to = date.plusDays(1);
		return orderRepository.findAllByDeliveryDateAndRiderIdOrNull(date, to, riderId);
	}

	/**
	 * Updates the delivery status of an order by marking it as delivered or
	 * undelivered.
	 *
	 * @param orderId   The ID of the order to update.
	 * @param delivered The delivery status to set for the order.
	 */
	public void markOrderDelivered(long orderId, boolean delivered) {
		OrderDelivery delivery = orderService.getOrder(orderId).getDelivery();
		delivery.setDelivered(delivered);
		deliveryRepository.save(delivery);
	}

}
