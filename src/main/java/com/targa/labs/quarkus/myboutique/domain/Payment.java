package com.targa.labs.quarkus.myboutique.domain;

import com.targa.labs.quarkus.myboutique.domain.enumeration.PaymentStatus;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.Objects;

/**
 * A Payment.
 */
@Entity
@Table(name = "payments")
public class Payment extends AbstractEntity {

    @Column(name = "paypal_payment_id")
    private String paypalPaymentId;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PaymentStatus status;

    @OneToOne
    @JoinColumn(unique = true)
    private Order order;

    public Payment() {
    }

    public Payment(String paypalPaymentId, @NotNull PaymentStatus status, Order order) {
        this.paypalPaymentId = paypalPaymentId;
        this.status = status;
        this.order = order;
    }

    public String getPaypalPaymentId() {
        return paypalPaymentId;
    }

    public void setPaypalPaymentId(String paypalPaymentId) {
        this.paypalPaymentId = paypalPaymentId;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Payment payment = (Payment) o;
        return Objects.equals(paypalPaymentId, payment.paypalPaymentId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(paypalPaymentId);
    }

    @Override
    public String toString() {
        return "Payment{" +
                "paypalPaymentId='" + paypalPaymentId + '\'' +
                ", status=" + status +
                ", order=" + order.getId() +
                '}';
    }
}
