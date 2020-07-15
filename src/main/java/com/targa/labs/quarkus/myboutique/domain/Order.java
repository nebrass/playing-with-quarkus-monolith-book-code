package com.targa.labs.quarkus.myboutique.domain;

import com.targa.labs.quarkus.myboutique.domain.enumeration.OrderStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.json.bind.annotation.JsonbTransient;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.Set;

/**
 * A Orders.
 */
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "orders")
public class Order extends AbstractEntity {

    @NotNull
    @Column(name = "total_price", precision = 10, scale = 2, nullable = false)
    private BigDecimal price;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OrderStatus status;

    @Column(name = "shipped")
    private ZonedDateTime shipped;

    @OneToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(unique = true)
    private Payment payment;

    @Embedded
    private Address shipmentAddress;

    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @JsonbTransient
    private Set<OrderItem> orderItems;

    @OneToOne(cascade = CascadeType.REMOVE)
    private Cart cart;

    public Order(@NotNull BigDecimal price, @NotNull OrderStatus status,
                 ZonedDateTime shipped, Payment payment, Address shipmentAddress,
                 Set<OrderItem> orderItems, Cart cart) {
        this.price = price;
        this.status = status;
        this.shipped = shipped;
        this.payment = payment;
        this.shipmentAddress = shipmentAddress;
        this.orderItems = orderItems;
        this.cart = cart;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return Objects.equals(price, order.price) &&
                status == order.status &&
                Objects.equals(shipped, order.shipped) &&
                Objects.equals(payment, order.payment) &&
                Objects.equals(shipmentAddress, order.shipmentAddress) &&
                Objects.equals(orderItems, order.orderItems) &&
                Objects.equals(cart, order.cart);
    }

    @Override
    public int hashCode() {
        return Objects.hash(price, status, shipped, payment, shipmentAddress, cart);
    }

    @Override
    public String toString() {
        return "Order{" +
                "totalPrice=" + price +
                ", status=" + status +
                ", shipped=" + shipped +
                ", payment=" + payment +
                ", shipmentAddress=" + shipmentAddress +
                ", cart=" + cart.getId() +
                '}';
    }
}
