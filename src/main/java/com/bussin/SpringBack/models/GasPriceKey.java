package com.bussin.SpringBack.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Embeddable
@EqualsAndHashCode
public class GasPriceKey implements Serializable {
    private LocalDateTime dateTime;
    @Enumerated(EnumType.STRING)
    private GasType gasType;
    private String company;

    public enum GasType {
        TypeDiesel,
        Type92,
        Type95,
        Type98,
        TypePremium
    }
}
