package com.bussin.SpringBack.models;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@EqualsAndHashCode
public class UserCreationDTO {
    //clientID
    private String password;

    private String username;

    private UserDTO userDTO;
}
