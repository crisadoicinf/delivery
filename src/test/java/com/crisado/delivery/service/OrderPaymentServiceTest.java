package com.crisado.delivery.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doAnswer;

import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
import com.crisado.delivery.model.BankAccount;
import com.crisado.delivery.model.CashPayment;
import com.crisado.delivery.model.Order;
import com.crisado.delivery.model.Rider;
import com.crisado.delivery.model.TransferencePayment;
import com.crisado.delivery.repository.BankAccountRepository;
import com.crisado.delivery.repository.OrderRepository;
import com.crisado.delivery.repository.RiderRepository;

@ExtendWith(MockitoExtension.class)
class OrderPaymentServiceTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private RiderRepository riderRepository;
    @Mock
    private BankAccountRepository bankAccountRepository;
    @Mock
    private Services services;
    @InjectMocks
    private OrderPaymentService orderPaymentService;

    @Test
    void getPaymentsThrowsExceptionIfOrderDoesNotExist() {
        var orderId = 1L;
        var orderPaymentDtoRequest = new GetOrderPaymentDtoRequest(orderId);

        when(orderRepository.findById(orderId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderPaymentService.getPayments(orderPaymentDtoRequest))
                .isInstanceOf(PaymentException.class)
                .hasMessage("Order not found")
                .hasCauseInstanceOf(OrderNotFoundException.class);
    }

    @Test
    void getPayments() {
        var orderId = 1L;
        var order = Order.builder()
                .payments(Set.of(
                        CashPayment.builder().build(),
                        TransferencePayment.builder().build()))
                .build();
        CashPaymentDto cashPaymentDto = new CashPaymentDto();
        TransferencePaymentDto transferencePaymentDto = new TransferencePaymentDto();

        when(orderRepository.findById(orderId))
                .thenReturn(Optional.of(order));
        when(services.map(any(CashPayment.class), eq(PaymentDto.class)))
                .thenReturn(cashPaymentDto);
        when(services.map(any(TransferencePayment.class), eq(PaymentDto.class)))
                .thenReturn(transferencePaymentDto);

        assertThat(orderPaymentService.getPayments(new GetOrderPaymentDtoRequest(orderId)))
                .containsExactlyInAnyOrder(cashPaymentDto, transferencePaymentDto);
    }

    @Test
    void createPaymentThrowsExceptionIfOrderDoesNotExist() {
        var orderId = 1L;
        var newPayment = new CashPaymentDto();
        var createPaymentDtoRequest = new CreatePaymentDtoRequest(orderId, newPayment);

        when(orderRepository.findById(orderId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderPaymentService.createPayment(createPaymentDtoRequest))
                .isInstanceOf(PaymentException.class)
                .hasMessage("Order not found")
                .hasCauseInstanceOf(OrderNotFoundException.class);
    }

    @Test
    void createPaymentThrowsExceptionIfPaymentTypeIsUnknown() {
        var orderId = 1L;
        var newPayment = new UnknownPaymentDto();
        var order = new Order();
        var createPaymentDtoRequest = new CreatePaymentDtoRequest(orderId, newPayment);

        when(orderRepository.findById(orderId))
                .thenReturn(Optional.of(order));

        assertThatThrownBy(() -> orderPaymentService.createPayment(createPaymentDtoRequest))
                .isInstanceOf(PaymentUnkownTypeException.class)
                .hasMessage("Unknown Payment Type");
    }

    @Test
    void createPaymentThrowsExceptionIfPaymentCouldNotBeCreated() {
        var orderId = 1L;
        var newPayment = CashPaymentDto.builder()
                .riderId(1)
                .build();
        var order = Order.builder()
                .payments(new HashSet<>())
                .build();
        var rider = new Rider();
        var createPaymentDtoRequest = new CreatePaymentDtoRequest(orderId, newPayment);

        when(orderRepository.findById(orderId))
                .thenReturn(Optional.of(order));
        when(riderRepository.findById(1))
                .thenReturn(Optional.of(rider));
        doAnswer(inv -> {
            order.getPayments().clear();
            return order;
        }).when(orderRepository).save(order);

        assertThatThrownBy(() -> orderPaymentService.createPayment(createPaymentDtoRequest))
                .isInstanceOf(PaymentException.class)
                .hasMessage("Payment could not be created");
    }

    @Test
    void createPaymentThrowsExceptionIfRiderDoesNotExist() {
        var orderId = 1L;
        var newPayment = CashPaymentDto.builder()
                .riderId(1)
                .build();
        var order = Order.builder()
                .payments(new HashSet<>())
                .build();
        var createPaymentDtoRequest = new CreatePaymentDtoRequest(orderId, newPayment);

        when(orderRepository.findById(orderId))
                .thenReturn(Optional.of(order));
        when(riderRepository.findById(1))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderPaymentService.createPayment(createPaymentDtoRequest))
                .isInstanceOf(PaymentException.class)
                .hasMessage("Rider not found")
                .hasCauseInstanceOf(RiderNotFoundException.class);
    }

    @Test
    void createPaymentThrowsExceptionIfBankAccountDoesNotExist() {
        var orderId = 1L;
        var newPayment = TransferencePaymentDto.builder()
                .bankAccountId(1)
                .build();
        var order = Order.builder()
                .payments(new HashSet<>())
                .build();
        var createPaymentDtoRequest = new CreatePaymentDtoRequest(orderId, newPayment);

        when(orderRepository.findById(orderId))
                .thenReturn(Optional.of(order));
        when(bankAccountRepository.findById(1))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderPaymentService.createPayment(createPaymentDtoRequest))
                .isInstanceOf(PaymentException.class)
                .hasMessage("Bank Account not found")
                .hasCauseInstanceOf(BankAccountNotFoundException.class);
    }

    @Test
    void createCashPayment() {
        var orderId = 1L;
        var newPayment = CashPaymentDto.builder()
                .riderId(1)
                .amount(10D)
                .build();
        var order = Order.builder()
                .payments(new HashSet<>())
                .build();
        var rider = new Rider();
        var paymentDto = new CashPaymentDto();

        when(orderRepository.findById(orderId))
                .thenReturn(Optional.of(order));
        when(riderRepository.findById(1))
                .thenReturn(Optional.of(rider));
        doAnswer(inv -> {
            order.getPayments()
                    .stream()
                    .forEach(p -> p.setId(1L));
            return order;
        }).when(orderRepository).save(order);
        when(services.map(any(CashPayment.class), eq(PaymentDto.class)))
                .thenReturn(paymentDto);

        assertThat(orderPaymentService.createPayment(new CreatePaymentDtoRequest(orderId, newPayment)))
                .isSameAs(paymentDto);
        verify(orderRepository).save(order);
        assertThat(order.getPayments())
                .element(0)
                .asInstanceOf(InstanceOfAssertFactories.type(CashPayment.class))
                .satisfies(payment -> {
                    assertThat(payment)
                            .extracting(CashPayment::getId, CashPayment::getAmount, CashPayment::getRider)
                            .containsExactly(1L, 10D, rider);
                    assertThat(payment.getDate()).isNotNull();
                });
    }

    @Test
    void createTransferencePayment() {
        var orderId = 1L;
        var newPayment = TransferencePaymentDto.builder()
                .bankAccountId(1)
                .amount(10D)
                .build();
        var order = Order.builder()
                .payments(new HashSet<>())
                .build();
        var bank = new BankAccount();
        var paymentDto = new TransferencePaymentDto();

        when(orderRepository.findById(orderId))
                .thenReturn(Optional.of(order));
        when(bankAccountRepository.findById(1))
                .thenReturn(Optional.of(bank));
        doAnswer(inv -> {
            order.getPayments()
                    .stream()
                    .forEach(p -> p.setId(1L));
            return order;
        }).when(orderRepository).save(order);
        when(services.map(any(TransferencePayment.class), eq(PaymentDto.class)))
                .thenReturn(paymentDto);

        assertThat(orderPaymentService.createPayment(new CreatePaymentDtoRequest(orderId, newPayment)))
                .isSameAs(paymentDto);
        verify(orderRepository).save(order);
        assertThat(order.getPayments())
                .element(0)
                .asInstanceOf(InstanceOfAssertFactories.type(TransferencePayment.class))
                .satisfies(payment -> {
                    assertThat(payment)
                            .extracting(TransferencePayment::getId, TransferencePayment::getAmount,
                                    TransferencePayment::getBankAccount)
                            .containsExactly(1L, 10D, bank);
                    assertThat(payment.getDate()).isNotNull();
                });

    }

    @Test
    void updatePaymentThrowsExceptionIfOrderDoesNotExist() {
        var orderId = 1L;
        var paymentId = 1L;
        var newPayment = CashPaymentDto.builder()
                .riderId(1)
                .amount(10D)
                .build();
        var updatePaymentDtoRequest = new UpdatePaymentDtoRequest(paymentId, orderId, newPayment);

        when(orderRepository.findById(orderId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderPaymentService.updatePayment(updatePaymentDtoRequest))
                .isInstanceOf(PaymentException.class)
                .hasMessage("Order not found")
                .hasCauseInstanceOf(OrderNotFoundException.class);
    }

    @Test
    void updatePaymentThrowsExceptionIfPaymentDoesNotExist() {
        var orderId = 1L;
        var paymentId = 1L;
        var newPayment = CashPaymentDto.builder()
                .riderId(1)
                .amount(10D)
                .build();
        var order = Order.builder()
                .payments(new HashSet<>())
                .build();
        var updatePaymentDtoRequest = new UpdatePaymentDtoRequest(paymentId, orderId, newPayment);

        when(orderRepository.findById(orderId))
                .thenReturn(Optional.of(order));

        assertThatThrownBy(() -> orderPaymentService.updatePayment(updatePaymentDtoRequest))
                .isInstanceOf(PaymentNotFoundException.class)
                .hasMessage("Payment not found");
    }

    @Test
    void updatePaymentThrowsExceptionIfPaymentTypeIsUnknown() {
        var orderId = 1L;
        var paymentId = 1L;
        var newPayment = new UnknownPaymentDto();
        var order = Order.builder()
                .payments(Set.of(CashPayment.builder().id(paymentId).build()))
                .build();
        var updatePaymentDtoRequest = new UpdatePaymentDtoRequest(paymentId, orderId, newPayment);

        when(orderRepository.findById(orderId))
                .thenReturn(Optional.of(order));

        assertThatThrownBy(() -> orderPaymentService.updatePayment(updatePaymentDtoRequest))
                .isInstanceOf(PaymentUnkownTypeException.class)
                .hasMessage("Unknown Payment Type");
    }

    @Test
    void updatePaymentThrowsExceptionIfCashPaymentTypeChanges() {
        var orderId = 1L;
        var paymentId = 1L;
        var newPayment = new TransferencePaymentDto();
        var order = Order.builder()
                .payments(Set.of(CashPayment.builder().id(paymentId).build()))
                .build();
        var updatePaymentDtoRequest = new UpdatePaymentDtoRequest(paymentId, orderId, newPayment);

        when(orderRepository.findById(orderId))
                .thenReturn(Optional.of(order));

        assertThatThrownBy(() -> orderPaymentService.updatePayment(updatePaymentDtoRequest))
                .isInstanceOf(PaymentTypeChangedException.class)
                .hasMessage("Payment Type can not be changed");
    }

    @Test
    void updatePaymentThrowsExceptionIfTransferencePaymentTypeChanges() {
        var orderId = 1L;
        var paymentId = 1L;
        var newPayment = new CashPaymentDto();
        var order = Order.builder()
                .payments(Set.of(TransferencePayment.builder().id(paymentId).build()))
                .build();
        var updatePaymentDtoRequest =new UpdatePaymentDtoRequest(paymentId, orderId, newPayment);

        when(orderRepository.findById(orderId))
                .thenReturn(Optional.of(order));

        assertThatThrownBy(() -> orderPaymentService.updatePayment(updatePaymentDtoRequest))
                .isInstanceOf(PaymentTypeChangedException.class)
                .hasMessage("Payment Type can not be changed");
    }

    @Test
    void updatePaymentCashPayment() {
        var orderId = 1L;
        var paymentId = 1L;
        var newPayment = CashPaymentDto.builder()
                .riderId(1)
                .amount(10D)
                .build();
        CashPayment cashPayment = CashPayment.builder()
                .id(paymentId)
                .date(ZonedDateTime.now())
                .rider(Rider.builder().id(2).build())
                .amount(5D)
                .build();
        var order = Order.builder()
                .payments(Set.of(cashPayment))
                .build();
        var rider = new Rider();
        var paymentDto = CashPaymentDto.builder().id(paymentId).build();

        when(orderRepository.findById(orderId))
                .thenReturn(Optional.of(order));
        when(riderRepository.findById(1))
                .thenReturn(Optional.of(rider));
        when(services.map(any(CashPayment.class), eq(PaymentDto.class)))
                .thenReturn(paymentDto);

        assertThat(orderPaymentService.updatePayment(new UpdatePaymentDtoRequest(paymentId, orderId, newPayment)))
                .isSameAs(paymentDto);
        verify(orderRepository).save(order);
        assertThat(cashPayment)
                .satisfies(payment -> {
                    assertThat(payment)
                            .extracting(CashPayment::getId, CashPayment::getAmount, CashPayment::getRider)
                            .containsExactly(1L, 10D, rider);
                    assertThat(payment.getDate()).isNotNull();
                });
    }

    @Test
    void updatePaymentTransferencePayment() {
        var orderId = 1L;
        var paymentId = 1L;
        var newPayment = TransferencePaymentDto.builder()
                .bankAccountId(1)
                .amount(10D)
                .build();
        TransferencePayment cashPayment = TransferencePayment.builder()
                .id(paymentId)
                .date(ZonedDateTime.now())
                .bankAccount(BankAccount.builder().id(2).build())
                .amount(5D)
                .build();
        var order = Order.builder()
                .payments(Set.of(cashPayment))
                .build();
        var bank = new BankAccount();
        var paymentDto = TransferencePaymentDto.builder().id(paymentId).build();

        when(orderRepository.findById(orderId))
                .thenReturn(Optional.of(order));
        when(bankAccountRepository.findById(1))
                .thenReturn(Optional.of(bank));
        when(services.map(any(TransferencePayment.class), eq(PaymentDto.class)))
                .thenReturn(paymentDto);

        assertThat(orderPaymentService.updatePayment(new UpdatePaymentDtoRequest(paymentId, orderId, newPayment)))
                .isSameAs(paymentDto);
        verify(orderRepository).save(order);
        assertThat(cashPayment)
                .satisfies(payment -> {
                    assertThat(payment)
                            .extracting(TransferencePayment::getId, TransferencePayment::getAmount,
                                    TransferencePayment::getBankAccount)
                            .containsExactly(1L, 10D, bank);
                    assertThat(payment.getDate()).isNotNull();
                });
    }

    private class UnknownPaymentDto extends PaymentDto {
    }
}
