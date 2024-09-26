package gr.technico.technikon.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Property implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(min = 20, max = 20, message = "E9 must contain 20 characters.")
    @NotNull
    @Column(unique = true)
    private String e9;

    @Column(name = "property_address", length = 50)
    private String propertyAddress;

    @Digits(integer = 4, fraction = 0)
    @Column(name = "construction_year")
    private int constructionYear;

    @Enumerated(EnumType.STRING)
    @Column(name = "property_type", nullable = false)
    private PropertyType propertyType;

    @NotNull
    private boolean isDeleted = false;

    @ManyToOne
    @JoinColumn(name = "owner_vat", referencedColumnName = "vat", nullable = false)
    private Owner owner;

    @JsonIgnore
    @OneToMany(mappedBy = "property", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Repair> repairs;

    @Override
    public String toString() {
        return "Property{" + "id=" + id + ", e9=" + e9 + ", propertyAddress=" + propertyAddress + ", constructionYear=" + constructionYear + ", propertyType=" + propertyType + ", owner=" + (owner != null ? owner.getVat() : "null") + ", isDeleted=" + isDeleted + '}';
    }
}
