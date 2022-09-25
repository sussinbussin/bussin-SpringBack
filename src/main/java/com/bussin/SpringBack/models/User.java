package com.bussin.SpringBack.models;

import lombok.*;
import org.hibernate.annotations.Type;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.Date;
import java.util.Set;
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
@EqualsAndHashCode
@Builder
@ToString
public class User implements Serializable {
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

    @OneToOne(mappedBy = "user",
            cascade = CascadeType.ALL,
            optional = true)
    private Driver driver;

    @OneToMany(mappedBy = "user")
    private Set<Ride> rides;

    public User(String nric, String name, String address, Date dob, String mobile, String email) {
        this.nric = nric;
        this.name = name;
        this.address = address;
        this.dob = dob;
        this.mobile = mobile;
        this.email = email;
        this.isDriver = false;
    }

    public void updateFromDTO(UserDTO userDTO) {
        this.nric = userDTO.getNric();
        this.name = userDTO.getName();
        this.address = userDTO.getAddress();
        this.dob = userDTO.getDob();
        this.mobile = userDTO.getMobile();
        this.email = userDTO.getEmail();
        this.isDriver = userDTO.getIsDriver();
    }
}
