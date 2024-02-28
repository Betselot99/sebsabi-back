package et.com.gebeya.safaricom.coreservice.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import et.com.gebeya.safaricom.coreservice.dto.requestDto.FormQuestionDto;
import et.com.gebeya.safaricom.coreservice.model.enums.QuestionType;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FormQuestion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank(message = "Question title is required")
    private String questionText;
    @Valid
    private QuestionType questionType;
    @OneToMany(mappedBy = "formQuestion", cascade = CascadeType.ALL)
    @Valid
    private List<MultipleChoiceOption> multipleChoiceOptions;
    private int ratingScale; // Add this field for rating scale

    @ManyToOne
    @JoinColumn(name = "form_id")
    @JsonIgnore
    @Valid
    private Form form;  // Add this field

    public FormQuestion(FormQuestionDto formQuestionDto){
        this.setQuestionText(formQuestionDto.getQuestionText());
        this.setQuestionType(formQuestionDto.getQuestionType());
    }
}
