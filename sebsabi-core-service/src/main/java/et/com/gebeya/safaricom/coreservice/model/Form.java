package et.com.gebeya.safaricom.coreservice.model;

import et.com.gebeya.safaricom.coreservice.dto.requestDto.FormDto;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Form {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank(message = "Title is required")
    private String title;
    @NotBlank(message = "Description is required")
    @Size(max = 250, message = "Description text must be at most 250 characters")
    private String description;
    @NotBlank(message = "Usage limit is required")
    private int usageLimit;
    private Status status;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "client_username")
    @Valid
    private Client client;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "form_id")
    @Valid
    private List<FormQuestion> questions;
    @OneToMany(mappedBy = "form", cascade = CascadeType.ALL, orphanRemoval = true)
    @Valid
    private List<UserResponse> userResponses;
    @OneToMany(mappedBy = "form", cascade = CascadeType.ALL)
    @Valid
    private List<Proposal> proposals;
    @ManyToOne
    @JoinColumn(name = "gig_worker_id")
    @Valid
    private GigWorker assignedGigWorker;
    public Form(FormDto formDto) {

        this.setDescription(formDto.getDescription());
        this.setTitle(formDto.getTitle());
        this.setUsageLimit(formDto.getUsageLimit());
        this.setStatus(formDto.getStatus());

    }
}
