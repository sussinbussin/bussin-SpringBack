package com.bussin.SpringBack.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class UserCreationDTO implements Serializable {
    private String password;

    private String username;

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
