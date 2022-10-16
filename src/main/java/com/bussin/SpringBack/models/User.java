package com.bussin.SpringBack.models;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import org.hibernate.Hibernate;
import org.hibernate.annotations.Type;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;
import java.util.Set;
import java.util.Objects;
import java.util.UUID;

@Entity(name = "bussinuser")
@Table(name = "bussinuser", uniqueConstraints = {
        @UniqueConstraint(name = "user_email_unique", columnNames = "email"),
        @UniqueConstraint(name = "mobile_unique", columnNames = "mobile"),
        @UniqueConstraint(name = "nric_unique", columnNames = "nric")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id")
public class User implements Serializable, Cloneable {
    @Id
    @GeneratedValue(generator = "uuid2")
    @Type(type = "org.hibernate.type.UUIDCharType")
    @Schema(description = "UUID of the User.",
            example = "844b8d14-ef82-4b27-b9b5-a5e765c1254f")
    private UUID id;

    @NotNull(message = "NRIC should not be empty")
    @Pattern(regexp = "^[a-zA-Z][0-9]{7}[a-zA-Z]$",
            message = "Nric must be in this format: T6969696A")
    @Schema(description = "NRIC of the User.", example = "S9912345A")
    private String nric;

    @NotNull(message = "Name should not be empty")
    @Schema(description = "User's name.", example = "Robert")
    private String name;

    @NotNull(message = "Date of Birth should not be empty")
    @DateTimeFormat(pattern = "dd/MM/yyyy")
    @Past
    @Schema(description = "User's date of birth",
            example = "2000-10-09T00:46:18.784Z")
    private Date dob;

    @NotNull(message = "Mobile number should not be empty")
    @Pattern(regexp = "^[8-9][0-9]{7}$",
            message = "Mobile must be in this format: 86969696")
    @Schema(description = "User's mobile number", example = "99887766")
    private String mobile;

    @NotNull(message = "Email should not be empty")
    @Email(regexp = "^[A-Z0-9._-]+@[A-Z0-9]+.[A-Z]{2,6}$", 
            flags = Pattern.Flag.CASE_INSENSITIVE, 
            message = "Email should be valid format: johnsus@email.xyz")
    @Schema(description = "User's email", example = "robert@gmail.com")
    private String email;

    @Schema(description = "Is this user a driver?", example = "true")
    private Boolean isDriver;

    @OneToOne(mappedBy = "user",
            cascade = CascadeType.ALL,
            optional = true)
    private Driver driver;

    @OneToMany(mappedBy = "user")
    private Set<Ride> rides;

    public User(String nric, String name, String address, Date dob, String mobile, String email) {
        this.nric = nric;
        this.name = name;
        this.dob = dob;
        this.mobile = mobile;
        this.email = email;
        this.isDriver = false;
    }

    public void updateFromDTO(UserDTO userDTO) {
        this.nric = userDTO.getNric();
        this.name = userDTO.getName();
        this.dob = userDTO.getDob();
        this.mobile = userDTO.getMobile();
        this.email = userDTO.getEmail();
        this.isDriver = userDTO.getIsDriver();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o))
            return false;
        User user = (User) o;
        return id != null && Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public User clone() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(
                    objectMapper.writeValueAsString(this), User.class);
        } catch (JsonProcessingException e) {
            return null;
        }
    }
}
