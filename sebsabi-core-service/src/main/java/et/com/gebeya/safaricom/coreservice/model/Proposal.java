package et.com.gebeya.safaricom.coreservice.model;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.validation.constraints.Size;


@Entity
@Table(name = "proposals")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Proposal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "form_id")
    @Valid
    private Form form;

    @ManyToOne
    @JoinColumn(name = "gig_worker_id")
    @Valid
    private GigWorker gigWorker;

    @Column(name = "rate_per_form")
    @Valid
    private Double ratePerForm;

    @Column(name = "proposal_text", columnDefinition = "TEXT")
    @NotBlank(message = "Proposal is required")
    @Size(max = 250, message = "Proposal text must be at most 250 characters")
    private String proposalText;
}

