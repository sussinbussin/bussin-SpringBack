package com.bussin.SpringBack.models.user;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.Hibernate;
import org.hibernate.annotations.Type;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

/**
 * A subset of User without sensitive information.
 */
@Getter
@Setter
@NoArgsConstructor
public class UserPublicDTO implements Serializable {
    @Id
    @GeneratedValue(generator = "uuid2")
    @Type(type = "org.hibernate.type.UUIDCharType")
    private UUID id;

    @NotNull(message = "Name should not be empty")
    private String name;

    @NotNull(message = "Mobile number should not be empty")
    @Pattern(regexp = "^[8-9][0-9]{7}$", message = "Mobile must be in this format: 86969696")
    private String mobile;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
            return false;
        }
        UserPublicDTO userDTO = (UserPublicDTO) o;
        return id != null && Objects.equals(id, userDTO.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
