package com.bussin.SpringBack.models;

import javax.persistence.*;
import javax.validation.*;
import javax.validation.constraints.*;

import java.math.*;
import java.security.*;
import java.util.*;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class RideDTO {
    @Id
    @GeneratedValue(generator = "uuid2")
    private UUID id;

    @NotNull(message = "How many passengers can this ride accommodate?")
    private Timestamp timestamp;

    @NotNull
    @Min(1)
    private Integer passengers;

    @NotNull
    @DecimalMin("0")
    private BigDecimal cost;
    
    public void validate() {
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<RideDTO>> violations = validator.validate(this);
        if (violations.size() > 0) {
            throw new ConstraintViolationException(violations);
        }
    }
}
