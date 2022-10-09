package com.bussin.SpringBack.models;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@Builder
@EqualsAndHashCode
public class UserCreationDTO implements Serializable {
    private String password;

    private String username;

    private UserDTO userDTO;
}
