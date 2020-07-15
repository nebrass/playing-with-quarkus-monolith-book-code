package com.targa.labs.quarkus.myboutique.service;

import com.targa.labs.quarkus.myboutique.domain.Order;
import com.targa.labs.quarkus.myboutique.domain.Payment;
import com.targa.labs.quarkus.myboutique.domain.enumeration.OrderStatus;
import com.targa.labs.quarkus.myboutique.domain.enumeration.PaymentStatus;
import com.targa.labs.quarkus.myboutique.repository.OrderRepository;
import com.targa.labs.quarkus.myboutique.repository.PaymentRepository;
import com.targa.labs.quarkus.myboutique.web.dto.PaymentDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
@Transactional
public class PaymentService {
    private final Logger log = LoggerFactory.getLogger(PaymentService.class);

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    public PaymentService(PaymentRepository paymentRepository, OrderRepository orderRepository) {
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
    }

    public static PaymentDto mapToDto(Payment payment, Long orderId) {
        if (payment != null) {
            return new PaymentDto(
                    payment.getId(),
                    payment.getPaypalPaymentId(),
                    payment.getStatus().name(),
                    orderId
            );
        }
        return null;
    }

    public List<PaymentDto> findByPriceRange(double max) {
        return this.orderRepository
                .findAllByPriceBetween(BigDecimal.ZERO, BigDecimal.valueOf(max))
                .stream()
                .map(order -> mapToDto(order.getPayment(), order.getId()))
                .collect(Collectors.toList());
    }

    public List<PaymentDto> findAll() {
        log.debug("Request to get all Payments");
        return this.paymentRepository.findAll()
                .stream()
                .map(payment -> findById(payment.getId()))
                .collect(Collectors.toList());
    }

    public PaymentDto findById(Long id) {
        log.debug("Request to get Payment : {}", id);
        Order order = this.orderRepository.findByPaymentId(id)
                .orElseThrow(() -> new IllegalStateException("No Order exists for the Payment ID " + id));

        return this.paymentRepository
                .findById(id)
                .map(payment -> mapToDto(payment, order.getId()))
                .orElse(null);
    }

    public PaymentDto create(PaymentDto paymentDto) {
        log.debug("Request to create Payment : {}", paymentDto);

        Order order =
                this.orderRepository
                        .findById(paymentDto.getOrderId())
                        .orElseThrow(() -> new IllegalStateException("The Order does not exist!"));

        order.setStatus(OrderStatus.PAID);

        Payment payment = this.paymentRepository.saveAndFlush(new Payment(
                paymentDto.getPaypalPaymentId(),
                PaymentStatus.valueOf(paymentDto.getStatus())
        ));

        this.orderRepository.saveAndFlush(order);

        return mapToDto(payment, order.getId());
    }

    public void delete(Long id) {
        log.debug("Request to delete Payment : {}", id);
        this.paymentRepository.deleteById(id);
    }
}