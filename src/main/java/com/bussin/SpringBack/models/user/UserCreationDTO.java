package com.bussin.SpringBack.models.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class UserCreationDTO implements Serializable {
    @NotNull
    private String password;

    @NotNull
    private String username;

    @NotNull
    private UserDTO userDTO;

    @Override
    public String toString() {
        return "UserCreationDTO{" +
                "password='" + password + '\'' +
                ", username='" + username + '\'' +
                ", userDTO=" + userDTO +
                '}';
    }
}
