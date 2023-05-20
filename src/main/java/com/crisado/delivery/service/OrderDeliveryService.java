package com.crisado.delivery.service;

import static java.util.stream.Collectors.toList;

import java.time.ZonedDateTime;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.crisado.delivery.dto.OrderSummaryDto;
import com.crisado.delivery.dto.RiderDto;
import com.crisado.delivery.exception.StateException;
import com.crisado.delivery.model.OrderDelivery;
import com.crisado.delivery.repository.OrderDeliveryRepository;
import com.crisado.delivery.repository.OrderRepository;
import com.crisado.delivery.repository.RiderRepository;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service
@Transactional
@AllArgsConstructor
public class OrderDeliveryService {

	private final OrderRepository orderRepository;
	private final OrderDeliveryRepository deliveryRepository;
	private final RiderRepository riderRepository;
	private final ModelMapper mapper;

	/**
	 * Retrieves all orders for a specific delivery date and rider ID.
	 * Also includes any orders that do not have a rider assigned.
	 *
	 * @param date    The delivery date to filter by.
	 * @param riderId The ID of the rider to filter by.
	 * 
	 * @return A list of orders.
	 */
	public List<OrderSummaryDto> getOrders(ZonedDateTime date, int riderId) {
		ZonedDateTime to = date.plusDays(1);
		return orderRepository.findAllByDeliveryDateAndRiderIdOrNull(date, to, riderId)
				.stream()
				.map(order -> mapper.map(order, OrderSummaryDto.class))
				.collect(toList());
	}

	/**
	 * Updates the delivery status of an order by marking it as delivered or
	 * undelivered.
	 *
	 * @param orderId   The ID of the order to update.
	 * @param delivered The delivery status to set for the order.
	 */
	public void setOrderDelivered(long orderId, boolean delivered) {
		OrderDelivery delivery = orderRepository.findById(orderId)
				.orElseThrow(() -> new StateException("Order not found"))
				.getDelivery();
		delivery.setDelivered(delivered);
		deliveryRepository.save(delivery);
	}

	/**
	 * Retrieves a list of riders for delivery.
	 *
	 * @return A list of riders.
	 */
	public List<RiderDto> getRiders() {
		return riderRepository.findAll().stream()
				.map(rider -> mapper.map(rider, RiderDto.class))
				.collect(toList());
	}

}
