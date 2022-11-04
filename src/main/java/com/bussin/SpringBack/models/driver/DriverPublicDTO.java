package com.bussin.SpringBack.models.driver;

import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@Builder
@EqualsAndHashCode
public class DriverPublicDTO extends DriverDTO implements Serializable {
    private UUID user;

    @JsonSetter
    public void setUser(UUID user) {
        this.user = user;
    }
}
