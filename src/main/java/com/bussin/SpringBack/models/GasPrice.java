package com.bussin.SpringBack.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.Hibernate;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class GasPrice implements Serializable {
    @EmbeddedId
    private GasPriceKey gasPriceKey;

    private BigDecimal price;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o))
            return false;
        GasPrice gasPrice = (GasPrice) o;
        return gasPriceKey != null && Objects.equals(gasPriceKey, gasPrice.gasPriceKey);
    }

    @Override
    public int hashCode() {
        return Objects.hash(gasPriceKey);
    }
}
