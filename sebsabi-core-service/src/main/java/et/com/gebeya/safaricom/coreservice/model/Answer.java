package et.com.gebeya.safaricom.coreservice.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
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
@Table(name = "answers")
public class Answer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "response_id")
    @JsonIgnore
    private UserResponse userResponse;

    @ManyToOne
    @JoinColumn(name = "question_id")
    @JsonIgnore
    private FormQuestion question;

    @ManyToMany
    @JoinTable(
            name = "answer_multiple_choice_option",
            joinColumns = @JoinColumn(name = "answer_id"),
            inverseJoinColumns = @JoinColumn(name = "option_id")
    )
    private List<MultipleChoiceOption> selectedOptions;

    private String answerText;

    private Integer rating; // Add this field for range questions

    // Constructors, getters, and setters
}
