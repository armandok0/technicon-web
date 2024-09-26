package gr.technico.technikon.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Owner implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(min = 9, max = 9)
    @Column(nullable = false, unique = true)
    private String vat;

    @Size(min = 1, max = 50)
    @NotNull
    private String name;

    @Size(min = 1, max = 50)
    @NotNull
    private String surname;

    @Size(max = 50)
    private String address;

    @Column(name = "phone_number", length = 14)
    private String phoneNumber;

    @Email
    @NotNull
    @Column(unique = true)
    private String email;

    @Column(length = 50, nullable = false, unique = true)
    private String username;

    @Size(min = 8, max = 50)
    @NotNull
    private String password;

    @NotNull
    private boolean isDeleted = false;

    @Enumerated(EnumType.STRING)
    @NotNull
    private Role role = Role.USER;

    public enum Role {
        ADMIN, USER
    }

    @JsonIgnore
    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Property> propertyList;
}
