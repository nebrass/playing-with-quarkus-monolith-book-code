package com.targa.labs.quarkushop.domain;

import com.targa.labs.quarkushop.domain.enumeration.CartStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.Objects;

/**
 * A Cart.
 */
@Getter
@Setter
@NoArgsConstructor
@ToString(callSuper = true)
@Entity
@Table(name = "carts")
public class Cart extends AbstractEntity {

    @ManyToOne
    private Customer customer;

    @NotNull
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CartStatus status;

    public Cart(Customer customer, @NotNull CartStatus status) {
        this.customer = customer;
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cart cart = (Cart) o;
        return Objects.equals(customer, cart.customer) &&
                status == cart.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(customer, status);
    }
}
