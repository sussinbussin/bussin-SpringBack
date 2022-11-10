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
}
