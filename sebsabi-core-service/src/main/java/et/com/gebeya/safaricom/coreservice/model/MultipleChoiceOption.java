package et.com.gebeya.safaricom.coreservice.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class MultipleChoiceOption {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank(message = "optionText is required")
    private String optionText;

    @ManyToOne
    @JoinColumn(name = "form_question_id")
    @JsonIgnore
    @Valid
    private FormQuestion formQuestion;
}