package com.bussin.SpringBack.models;

import lombok.*;
import org.hibernate.Hibernate;
import org.hibernate.annotations.Type;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Table(uniqueConstraints = {
        @UniqueConstraint(name = "user_email_unique", columnNames = "email"),
        @UniqueConstraint(name = "mobile_unique", columnNames = "mobile"),
        @UniqueConstraint(name = "nric_unique", columnNames = "nric")
})
@Getter
@Setter
@AllArgsConstructor
@Builder
public class UserDTO implements Serializable {
    @Id
    @GeneratedValue(generator = "uuid2")
    @Type(type = "org.hibernate.type.UUIDCharType")
    private UUID id;

    @NotNull(message = "NRIC should not be empty")
    @Pattern(regexp = "^[a-zA-Z][0-9]{7}[a-zA-Z]$", message = "Nric must be in this format: T6969696A")
    private String nric;

    @NotNull(message = "Name should not be empty")
    private String name;

    @NotNull(message = "Address should not be empty")
    private String address;

    @NotNull(message = "Date of Birth should not be empty")
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    @Past
    private Date dob;

    @NotNull(message = "Mobile number should not be empty")
    @Pattern(regexp = "^[8-9][0-9]{7}$", message = "Mobile must be in this format: 86969696")
    private String mobile;

    @NotNull(message = "Email should not be empty")
    @Email(message = "Email should be valid format: johnsus@email.xyz")
    private String email;

    private Boolean isDriver;

    public void validate(){
        Validator validator =
                Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<UserDTO>> violations =
                validator.validate(this);
        if(violations.size()>0){
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
        UserDTO userDTO = (UserDTO) o;
        return id != null && Objects.equals(id, userDTO.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
