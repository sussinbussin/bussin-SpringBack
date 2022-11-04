package com.bussin.SpringBack.models.user;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.Hibernate;
import org.hibernate.annotations.Type;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * A subset of User without sensitive information.
 */
@Getter
@Setter
@NoArgsConstructor
@Builder
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

    /**
     * Check if there is any constraint violations during input
     */
    public void validate() {
        Validator validator =
                Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<UserPublicDTO>> violations =
                validator.validate(this);
        if (violations.size() > 0) {
            throw new ConstraintViolationException(violations);
        }
    }

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

    @JsonCreator
    public UserPublicDTO(@JsonProperty("id") UUID id,
                         @JsonProperty("name") String name,
                         @JsonProperty("mobile") String mobile) {
        this.id = id;
        this.name = name;
        this.mobile = mobile;
    }
}
