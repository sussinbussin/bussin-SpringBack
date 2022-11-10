package com.bussin.SpringBack.models.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Model used to create a User object
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserCreationDTO implements Serializable {
    @NotNull
    private String password;

    @NotNull
    private String username;

    @NotNull
    private UserDTO userDTO;
}
