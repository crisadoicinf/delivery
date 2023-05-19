package com.crisado.delivery.service;

import static java.util.stream.Collectors.toList;

import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.crisado.delivery.dto.CashPaymentDto;
import com.crisado.delivery.dto.PaymentDto;
import com.crisado.delivery.dto.TransferencePaymentDto;
import com.crisado.delivery.exception.ArgumentException;
import com.crisado.delivery.exception.StateException;
import com.crisado.delivery.model.CashPayment;
import com.crisado.delivery.model.Order;
import com.crisado.delivery.model.Payment;
import com.crisado.delivery.model.TransferencePayment;
import com.crisado.delivery.repository.BankAccountRepository;
import com.crisado.delivery.repository.OrderRepository;
import com.crisado.delivery.repository.RiderRepository;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service
@Transactional
@AllArgsConstructor
public class OrderPaymentService {

    private final OrderRepository orderRepository;
    private final RiderRepository riderRepository;
    private final BankAccountRepository bankAccountRepository;
    private final ModelMapper mapper;

    /**
     * Retrieves a list of payments associated with a given order.
     *
     * @param orderId The ID of the order.
     * @return A list of payments associated with the specified order.
     */
    public List<PaymentDto> getPayments(long orderId) {
        return getOrder(orderId)
                .getPayments()
                .stream()
                .map(payment -> mapper.map(payment, PaymentDto.class))
                .collect(toList());
    }

    /**
     * Creates a new payment for a given order.
     *
     * @param orderId    The ID of the order.
     * @param newPayment The request for the new payment.
     * @return The created payment.
     * @throws ArgumentException if the payment type is unknown.
     * @throws StateException    if the payment could not be created.
     */
    public PaymentDto createPayment(long orderId, PaymentDto newPayment) {
        Order order = getOrder(orderId);
        Payment payment;
        if (newPayment instanceof CashPaymentDto newCashPayment) {
            payment = updateCashPayment(new CashPayment(), newCashPayment);
        } else if (newPayment instanceof TransferencePaymentDto newTransferencePayment) {
            payment = updateTransferencePayment(new TransferencePayment(), newTransferencePayment);
        } else {
            throw new ArgumentException("Unknown Payment Type");
        }
        updatePayment(payment, newPayment);
        payment.setDate(ZonedDateTime.now());
        var prevPayments = new HashSet<>(order.getPayments());
        order.getPayments().add(payment);
        orderRepository.save(order);
        payment = order.getPayments()
                .stream()
                .filter(p -> !prevPayments.contains(p))
                .findFirst()
                .orElseThrow(() -> new StateException("Payment could not be created"));
        return mapper.map(payment, PaymentDto.class);
    }

    /**
     * Updates an existing payment for a given order.
     *
     * @param orderId    The ID of the order.
     * @param paymentId  The ID of the payment to update.
     * @param newPayment The request the updated payment.
     * @return The updated PaymentDto object.
     * @throws StateException    if the payment is not found or the payment type
     *                           cannot be changed.
     * @throws ArgumentException if the payment type is unknown.
     */
    public PaymentDto updatePayment(long orderId, long paymentId, PaymentDto newPayment) {
        Order order = getOrder(orderId);
        Payment payment = order.getPayments()
                .stream()
                .filter(p -> paymentId == p.getId())
                .findFirst()
                .orElseThrow(() -> new StateException("Payment not found"));
        if (newPayment instanceof CashPaymentDto newCashPayment) {
            if (!(payment instanceof CashPayment cashPayment))
                throw new StateException("Payment Type can not be changed");
            updateCashPayment(cashPayment, newCashPayment);
        } else if (newPayment instanceof TransferencePaymentDto newTransferencePayment) {
            if (!(payment instanceof TransferencePayment transferencePayment))
                throw new StateException("Payment Type can not be changed");
            updateTransferencePayment(transferencePayment, newTransferencePayment);
        } else {
            throw new ArgumentException("Unknown Payment Type");
        }
        updatePayment(payment, newPayment);
        orderRepository.save(order);
        return mapper.map(payment, PaymentDto.class);
    }

    private Order getOrder(long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new StateException("Order not found"));
    }

    private Payment updatePayment(Payment payment, PaymentDto newPayment) {
        payment.setAmount(newPayment.getAmount());
        return payment;
    }

    private Payment updateCashPayment(CashPayment cashPayment, CashPaymentDto newPayment) {
        cashPayment.setRider(riderRepository.findById(newPayment.getRiderId())
                .orElseThrow(() -> new StateException("Rider not found")));
        return cashPayment;
    }

    private Payment updateTransferencePayment(TransferencePayment transferencePayment,
            TransferencePaymentDto newPayment) {
        transferencePayment.setBankAccount(bankAccountRepository.findById(newPayment.getBankAccountId())
                .orElseThrow(() -> new StateException("Bank Account not found")));
        return transferencePayment;
    }

}
