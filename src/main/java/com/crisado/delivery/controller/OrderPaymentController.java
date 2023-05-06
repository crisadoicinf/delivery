package com.crisado.delivery.controller;

import com.crisado.delivery.dto.CashPaymentList;
import com.crisado.delivery.dto.PaymentList;
import com.crisado.delivery.dto.TransferencePaymentList;
import com.crisado.delivery.model.CashPayment;
import com.crisado.delivery.model.Order;
import com.crisado.delivery.model.Payment;
import com.crisado.delivery.model.TransferencePayment;
import com.crisado.delivery.repository.BankAccountRepository;
import com.crisado.delivery.repository.OrderRepository;
import com.crisado.delivery.repository.RiderRepository;
import com.crisado.delivery.service.OrderService;

import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import static java.util.stream.Collectors.toList;

import java.time.ZonedDateTime;

@AllArgsConstructor
@RestController
@RequestMapping("/api/orders/{orderId}")
public class OrderPaymentController {

    private final OrderRepository orderRepository;
    private final OrderService orderService;
    private final RiderRepository riderRepository;
    private final BankAccountRepository bankAccountRepository;
    private final ModelMapper mapper;

    @GetMapping("/payments")
    public List<PaymentList> getPayments(@PathVariable long orderId) {
        Order order = orderService.getOrder(orderId);
        return order.getPayments()
                .stream()
                .map(payment -> mapper.map(payment, PaymentList.class))
                .collect(toList());
    }

    @PostMapping("/payments")
    public PaymentList createPayment(
            @PathVariable long orderId,
            @RequestBody PaymentList newPayment) {
        Order order = orderService.getOrder(orderId);
        Payment payment;
        if (newPayment instanceof CashPaymentList newCashPayment) {
            payment = updatePayment(new CashPayment(), newCashPayment);
        } else if (newPayment instanceof TransferencePaymentList newTransferencePayment) {
            payment = updatePayment(new TransferencePayment(), newTransferencePayment);
        } else {
            throw new IllegalArgumentException("Unknown Payment Type");
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
                .orElseThrow(() -> new IllegalArgumentException("Payment not found"));
        return mapper.map(payment, PaymentList.class);
    }

    @PutMapping("/payments/{paymentId}")
    public PaymentList updatePayment(
            @PathVariable long orderId,
            @PathVariable long paymentId,
            @RequestBody PaymentList newPayment) {
        Order order = orderService.getOrder(orderId);
        Payment payment = order.getPayments()
                .stream()
                .filter(p -> paymentId == p.getId())
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Payment not found"));
        if (newPayment instanceof CashPaymentList newCashPayment
                && payment instanceof CashPayment cashPayment) {
            updatePayment(cashPayment, newCashPayment);
        } else if (newPayment instanceof TransferencePaymentList newTransferencePayment
                && payment instanceof TransferencePayment transferencePayment) {
            updatePayment(transferencePayment, newTransferencePayment);
        }
        updatePayment(payment, newPayment);
        orderRepository.save(order);
        return mapper.map(payment, PaymentList.class);
    }

    private Payment updatePayment(Payment payment, PaymentList newPayment) {
        payment.setAmount(newPayment.getAmount());
        return payment;
    }

    private Payment updatePayment(TransferencePayment transferencePayment, TransferencePaymentList newPayment) {
        transferencePayment.setBankAccount(bankAccountRepository.findById(newPayment.getBankAccountId())
                .orElseThrow(() -> new IllegalArgumentException("Bank Account not found")));
        return transferencePayment;
    }

    private Payment updatePayment(CashPayment cashPayment, CashPaymentList newPayment) {
        cashPayment.setRider(riderRepository.findById(newPayment.getRiderId())
                .orElseThrow(() -> new IllegalArgumentException("Payment not found")));
        return cashPayment;
    }

}
