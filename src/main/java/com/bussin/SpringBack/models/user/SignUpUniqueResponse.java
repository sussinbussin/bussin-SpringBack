package com.bussin.SpringBack.models.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Objects;

/**
 * Response to a query checking if the provided credentials are unique
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SignUpUniqueResponse implements Serializable {
    private boolean nricUnique;
    private boolean mobileUnique;
    private boolean emailUnique;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SignUpUniqueResponse that = (SignUpUniqueResponse) o;
        return isNricUnique() == that.isNricUnique() && isMobileUnique() == that.isMobileUnique() && isEmailUnique() == that.isEmailUnique();
    }

    @Override
    public int hashCode() {
        return Objects.hash(isNricUnique(), isMobileUnique(), isEmailUnique());
    }
}
