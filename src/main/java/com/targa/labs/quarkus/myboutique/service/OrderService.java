package com.targa.labs.quarkus.myboutique.service;

import com.targa.labs.quarkus.myboutique.domain.Address;
import com.targa.labs.quarkus.myboutique.domain.Cart;
import com.targa.labs.quarkus.myboutique.domain.Order;
import com.targa.labs.quarkus.myboutique.domain.enumeration.OrderStatus;
import com.targa.labs.quarkus.myboutique.repository.OrderRepository;
import com.targa.labs.quarkus.myboutique.repository.PaymentRepository;
import com.targa.labs.quarkus.myboutique.web.dto.OrderDto;
import com.targa.labs.quarkus.myboutique.web.dto.OrderItemDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@ApplicationScoped
@Transactional
public class OrderService {

    private final Logger log = LoggerFactory.getLogger(OrderService.class);

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;

    public OrderService(OrderRepository orderRepository, PaymentRepository paymentRepository) {
        this.orderRepository = orderRepository;
        this.paymentRepository = paymentRepository;
    }

    public static OrderDto mapToDto(Order order) {
        if (order != null) {

            Set<OrderItemDto> orderItems = order
                    .getOrderItems()
                    .stream()
                    .map(OrderItemService::mapToDto)
                    .collect(Collectors.toSet());

            return new OrderDto(
                    order.getId(),
                    order.getPrice(),
                    order.getStatus().name(),
                    order.getShipped(),
                    order.getPayment() != null ? order.getPayment().getId() : null,
                    AddressService.mapToDto(order.getShipmentAddress()),
                    orderItems,
                    CartService.mapToDto(order.getCart())
            );
        }
        return null;
    }

    public List<OrderDto> findAll() {
        log.debug("Request to get all Orders");
        return this.orderRepository.findAll()
                .stream()
                .map(OrderService::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public OrderDto findById(Long id) {
        log.debug("Request to get Order : {}", id);
        return this.orderRepository.findById(id).map(OrderService::mapToDto).orElse(null);
    }

    public List<OrderDto> findAllByUser(Long id) {
        return this.orderRepository.findByCartCustomerId(id)
                .stream()
                .map(OrderService::mapToDto)
                .collect(Collectors.toList());
    }

    public OrderDto create(OrderDto orderDto) {
        log.debug("Request to create Order : {}", orderDto);
        return mapToDto(
                this.orderRepository.save(
                        new Order(
                                BigDecimal.ZERO,
                                OrderStatus.CREATION,
                                null,
                                null,
                                AddressService.createFromDto(orderDto.getShipmentAddress()),
                                Collections.emptySet(),
                                null
                        )
                )
        );
    }

    public Order create(Cart cart, Address address) {
        log.debug("Request to create Order with a Cart : {}", cart);
        return this.orderRepository.save(
                new Order(
                        BigDecimal.ZERO,
                        OrderStatus.CREATION,
                        null,
                        null,
                        address,
                        Collections.emptySet(),
                        cart
                )
        );
    }

    @Transactional
    public void delete(Long id) {
        log.debug("Request to delete Order : {}", id);
        Order order = this.orderRepository.findById(id).orElseThrow(() -> new IllegalStateException(""));
        paymentRepository.delete(order.getPayment());
        orderRepository.delete(order);
    }

    public boolean existsById(Long id) {
        return this.orderRepository.existsById(id);
    }
}
