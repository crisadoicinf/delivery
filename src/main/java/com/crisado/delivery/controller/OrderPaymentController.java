package com.crisado.delivery.controller;

import com.crisado.delivery.dto.BankAccountDto;
import com.crisado.delivery.dto.CreatePaymentDtoRequest;
import com.crisado.delivery.dto.GetOrderPaymentDtoRequest;
import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.crisado.delivery.dto.PaymentDto;
import com.crisado.delivery.dto.UpdatePaymentDtoRequest;
import com.crisado.delivery.service.OrderPaymentService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("/api/orders")
public class OrderPaymentController {

    private final OrderPaymentService orderPaymentService;

    @GetMapping("/bank-accounts")
    public ResponseEntity<List<BankAccountDto>> getBankAccounts() {
        var bankAccounts = orderPaymentService.getBankAccounts();
        return ResponseEntity.ok(bankAccounts);
    }

    @GetMapping("/{orderId}/payments")
    public ResponseEntity<List<PaymentDto>> getPayments(@PathVariable long orderId) {
        var getOrderPaymentRequest = new GetOrderPaymentDtoRequest(orderId);
        List<PaymentDto> payments = orderPaymentService.getPayments(getOrderPaymentRequest);
        return ResponseEntity.ok(payments);
    }

    @PostMapping("/{orderId}/payments")
    public ResponseEntity<PaymentDto> createPayment(
            @PathVariable long orderId,
            @RequestBody PaymentDto newPayment) {
        var createPaymentDtoRequest = new CreatePaymentDtoRequest(orderId, newPayment);
        PaymentDto payment = orderPaymentService.createPayment(createPaymentDtoRequest);
        return ResponseEntity
                .created(URI.create("/api/orders/" + orderId + "/payments/" + payment.getId()))
                .body(payment);
    }

    @PutMapping("/{orderId}/payments/{paymentId}")
    public ResponseEntity<PaymentDto> updatePayment(
            @PathVariable long orderId,
            @PathVariable long paymentId,
            @RequestBody PaymentDto newPayment) {
        var updatePaymentDtoRequest = new UpdatePaymentDtoRequest(orderId, paymentId, newPayment);
        PaymentDto payment = orderPaymentService.updatePayment(updatePaymentDtoRequest);
        return ResponseEntity.ok(payment);
    }
}
