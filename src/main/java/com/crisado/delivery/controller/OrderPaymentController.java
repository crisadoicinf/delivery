package com.crisado.delivery.controller;

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
import com.crisado.delivery.service.OrderPaymentService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("/api/orders/{orderId}")
public class OrderPaymentController {

    private final OrderPaymentService orderPaymentService;

    @GetMapping("/payments")
    public ResponseEntity<List<PaymentDto>> getPayments(@PathVariable long orderId) {
        List<PaymentDto> payments = orderPaymentService.getPayments(orderId);
        return ResponseEntity.ok(payments);
    }

    @PostMapping("/payments")
    public ResponseEntity<PaymentDto> createPayment(
            @PathVariable long orderId,
            @RequestBody PaymentDto newPayment) {
        PaymentDto payment = orderPaymentService.createPayment(orderId, newPayment);
        return ResponseEntity
                .created(URI.create("/api/orders/" + orderId + "/payments/" + payment.getId()))
                .body(payment);
    }

    @PutMapping("/payments/{paymentId}")
    public ResponseEntity<PaymentDto> updatePayment(
            @PathVariable long orderId,
            @PathVariable long paymentId,
            @RequestBody PaymentDto newPayment) {
        PaymentDto payment = orderPaymentService.updatePayment(orderId, paymentId, newPayment);
        return ResponseEntity.ok(payment);
    }
}
