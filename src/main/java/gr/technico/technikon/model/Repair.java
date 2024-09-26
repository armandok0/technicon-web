package gr.technico.technikon.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Repair implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "repair_type", nullable = false)
    private RepairType repairType;

    @Column(name = "short_description", length = 100)
    private String shortDescription;

    @PastOrPresent
    @NotNull
    @Column(name = "submission_date")
    private Date submissionDate;

    @Size(max = 400)
    private String description;

    @FutureOrPresent
    @Column(name = "proposed_start_date")
    private Date proposedStartDate;

    @FutureOrPresent
    @Column(name = "proposed_end_date")
    private Date proposedEndDate;

    @DecimalMin(value = "0.0")
    @Column(name = "proposed_cost")
    private BigDecimal proposedCost;

    @Column(name = "acceptance_status")
    private Boolean acceptanceStatus;

    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(name = "repair_status")
    private RepairStatus repairStatus = RepairStatus.PENDING;

    @Column(name = "actual_start_date")
    private Date actualStartDate;

    @Column(name = "actual_end_date")
    private Date actualEndDate;

    @NotNull
    private boolean isDeleted = false;

    @ManyToOne
    private Property property;

    @Override
    public String toString() {
        return "Repair{" + "id=" + id + ", repairType=" + repairType + ", shortDescription=" + shortDescription + ", submissionDate=" + submissionDate + ", description=" + description + ", proposedStartDate=" + proposedStartDate + ", proposedEndDate=" + proposedEndDate + ", proposedCost=" + proposedCost + ", acceptanceStatus=" + acceptanceStatus + ", repairStatus=" + repairStatus + ", actualStartDate=" + actualStartDate + ", actualEndDate=" + actualEndDate + ", isDeleted=" + isDeleted + '}' + "\n";
    }

}
