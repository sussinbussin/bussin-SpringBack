package com.bussin.SpringBack.models.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

/**
 * A query checking if the provided credentials are unique
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignUpUniqueRequest implements Serializable {
    private String nric;
    private String mobile;
    private String email;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SignUpUniqueRequest that = (SignUpUniqueRequest) o;
        return Objects.equals(this.getNric(), that.getNric()) && Objects.equals(this.getMobile(), that.getMobile()) && Objects.equals(this.getEmail(), that.getEmail());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getNric(), this.getMobile(), this.getEmail());
    }
}
