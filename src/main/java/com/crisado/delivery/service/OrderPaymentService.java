package com.crisado.delivery.service;

import com.crisado.delivery.dto.BankAccountDto;

import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.List;

import org.springframework.stereotype.Service;

import com.crisado.delivery.dto.CashPaymentDto;
import com.crisado.delivery.dto.CreatePaymentDtoRequest;
import com.crisado.delivery.dto.GetOrderPaymentDtoRequest;
import com.crisado.delivery.dto.PaymentDto;
import com.crisado.delivery.dto.TransferencePaymentDto;
import com.crisado.delivery.dto.UpdatePaymentDtoRequest;
import com.crisado.delivery.exception.BankAccountNotFoundException;
import com.crisado.delivery.exception.OrderNotFoundException;
import com.crisado.delivery.exception.PaymentException;
import com.crisado.delivery.exception.PaymentNotFoundException;
import com.crisado.delivery.exception.PaymentTypeChangedException;
import com.crisado.delivery.exception.PaymentUnkownTypeException;
import com.crisado.delivery.exception.RiderNotFoundException;
import com.crisado.delivery.model.CashPayment;
import com.crisado.delivery.model.Order;
import com.crisado.delivery.model.Payment;
import com.crisado.delivery.model.TransferencePayment;
import com.crisado.delivery.repository.BankAccountRepository;
import com.crisado.delivery.repository.OrderRepository;
import com.crisado.delivery.repository.RiderRepository;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

import static java.util.stream.Collectors.toList;

@Service
@Transactional
@AllArgsConstructor
public class OrderPaymentService {

    private final Services services;
    private final OrderRepository orderRepository;
    private final RiderRepository riderRepository;
    private final BankAccountRepository bankAccountRepository;

    public List<BankAccountDto> getBankAccounts() {
        return bankAccountRepository.findAll()
                .stream()
                .map(bank -> services.map(bank, BankAccountDto.class))
                .toList();
    }

    /**
     * Retrieves a list of payments associated with the specified order ID.
     *
     * @param request the request object containing the necessary parameters
     * @return a list of payments
     */
    public List<PaymentDto> getPayments(GetOrderPaymentDtoRequest request) {
        services.validate("Get Payments Request", request);
        var orderId = request.getOrderId();

        return findOrderById(orderId)
                .getPayments()
                .stream()
                .map(payment -> services.map(payment, PaymentDto.class))
                .collect(toList());
    }

    /**
     * Creates a new payment for the specified order based on the provided
     * request.
     *
     * @param createPaymentRequest the request object containing the necessary parameters
     * @return the created payment as a PaymentDto object
     * @throws PaymentUnkownTypeException if the new payment type is unknown
     * @throws PaymentException if the payment could not be created
     */
    public PaymentDto createPayment(CreatePaymentDtoRequest createPaymentRequest) {
        services.validate("Create Payment Request", createPaymentRequest);
        var orderId = createPaymentRequest.getOrderId();
        var newPayment = createPaymentRequest.getNewPayment();

        var order = findOrderById(orderId);
        Payment payment;
        if (newPayment instanceof CashPaymentDto newCashPayment) {
            payment = updateCashPayment(new CashPayment(), newCashPayment);
        } else if (newPayment instanceof TransferencePaymentDto newTransferencePayment) {
            payment = updateTransferencePayment(new TransferencePayment(), newTransferencePayment);
        } else {
            throw new PaymentUnkownTypeException();
        }
        updateAbstractPayment(payment, newPayment);
        payment.setDate(ZonedDateTime.now());
        var prevPayments = new HashSet<>(order.getPayments());
        order.getPayments().add(payment);
        saveOrder(order);
        payment = order.getPayments()
                .stream()
                .filter(p -> !prevPayments.contains(p))
                .findFirst()
                .orElseThrow(() -> new PaymentException("Payment could not be created"));
        return services.map(payment, PaymentDto.class);
    }

    /**
     * Updates an existing payment for the specified order based on the provided
     * request.
     *
     * @param request the request object containing the necessary parameters
     * @return the updated payment as a PaymentDto object
     * @throws PaymentNotFoundException if the payment with the specified ID is
     * not found
     * @throws PaymentTypeChangedException if the payment type cannot be changed
     * @throws PaymentUnkownTypeException if the new payment type is unknown
     */
    public PaymentDto updatePayment(UpdatePaymentDtoRequest updatePaymentRequest) {
        services.validate("Update Payment Request", updatePaymentRequest);
        var orderId = updatePaymentRequest.getOrderId();
        var paymentId = updatePaymentRequest.getPaymentId();
        var newPayment = updatePaymentRequest.getNewPayment();

        Order order = findOrderById(orderId);
        Payment payment = order.getPayments()
                .stream()
                .filter(p -> paymentId == p.getId())
                .findFirst()
                .orElseThrow(() -> new PaymentNotFoundException(paymentId));
        if (newPayment instanceof CashPaymentDto newCashPayment) {
            if (!(payment instanceof CashPayment cashPayment)) {
                throw new PaymentTypeChangedException();
            }
            updateCashPayment(cashPayment, newCashPayment);
        } else if (newPayment instanceof TransferencePaymentDto newTransferencePayment) {
            if (!(payment instanceof TransferencePayment transferencePayment)) {
                throw new PaymentTypeChangedException();
            }
            updateTransferencePayment(transferencePayment, newTransferencePayment);
        } else {
            throw new PaymentUnkownTypeException();
        }
        updateAbstractPayment(payment, newPayment);
        saveOrder(order);
        return services.map(payment, PaymentDto.class);
    }

    private Order findOrderById(long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new PaymentException(new OrderNotFoundException(id)));
    }

    private Payment updateAbstractPayment(Payment payment, PaymentDto newPayment) {
        services.validate("Update Payment Request", newPayment);
        payment.setAmount(newPayment.getAmount());
        return payment;
    }

    private Payment updateCashPayment(CashPayment cashPayment, CashPaymentDto newPayment) {
        services.validate("Cash Payment Request", newPayment);
        cashPayment.setRider(riderRepository.findById(newPayment.getRiderId())
                .orElseThrow(() -> new PaymentException(new RiderNotFoundException(newPayment.getRiderId()))));
        return cashPayment;
    }

    private Payment updateTransferencePayment(TransferencePayment transferencePayment, TransferencePaymentDto newPayment) {
        services.validate("Transaction Payment Request", newPayment);
        transferencePayment.setBankAccount(bankAccountRepository.findById(newPayment.getBankAccountId())
                .orElseThrow(() -> new PaymentException(new BankAccountNotFoundException(newPayment.getBankAccountId()))));
        return transferencePayment;
    }

    private void saveOrder(Order order) {
        orderRepository.save(order);
    }

}
