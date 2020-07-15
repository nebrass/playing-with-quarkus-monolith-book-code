package com.targa.labs.quarkus.myboutique.domain;

import com.targa.labs.quarkus.myboutique.domain.enumeration.PaymentStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "payments")
public class Payment extends AbstractEntity {

    @Column(name = "paypal_payment_id")
    private String paypalPaymentId;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PaymentStatus status;

    public Payment(String paypalPaymentId, @NotNull PaymentStatus status) {
        this.paypalPaymentId = paypalPaymentId;
        this.status = status;
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
                '}';
    }
}
