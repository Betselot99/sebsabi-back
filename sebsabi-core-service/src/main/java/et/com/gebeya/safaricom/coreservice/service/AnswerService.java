package et.com.gebeya.safaricom.coreservice.service;

import et.com.gebeya.safaricom.coreservice.dto.responseDto.AnswerAnalysisDTO;
import et.com.gebeya.safaricom.coreservice.dto.responseDto.MultipleChoiceOptionDTO;
import et.com.gebeya.safaricom.coreservice.dto.responseDto.OptionSelectionCountDTO;
import et.com.gebeya.safaricom.coreservice.dto.responseDto.QuestionAnalysisDTO;
import et.com.gebeya.safaricom.coreservice.model.Answer;
import et.com.gebeya.safaricom.coreservice.model.Form;
import et.com.gebeya.safaricom.coreservice.model.FormQuestion;
import et.com.gebeya.safaricom.coreservice.model.enums.Status;
import et.com.gebeya.safaricom.coreservice.model.enums.QuestionType;
import et.com.gebeya.safaricom.coreservice.repository.AnswerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
@RequiredArgsConstructor
@Service
public class AnswerService {
    private final AnswerRepository answerRepository;
    private final FormService formService;

    public Answer saveAnswer(Answer answer) {
        return answerRepository.save(answer);
    }

    public AnswerAnalysisDTO analyzeAnswers(Long formId, Long clientId) throws AccessDeniedException {
        Form form = formService.getFormForClientByFormId(formId, clientId);
        List<Answer> answers = answerRepository.findByUserResponse_Form_Id(formId);

        if (form.getStatus() != Status.Paid) {
            throw new RuntimeException("Form is not completed yet");
        }

        List<QuestionAnalysisDTO> questionAnalysis = form.getQuestions().stream()
                .map(question -> {
                    QuestionAnalysisDTO analysis = new QuestionAnalysisDTO();
                    analysis.setId(question.getId());
                    analysis.setQuestionText(question.getQuestionText());
                    analysis.setQuestionType(question.getQuestionType().toString());
                    analysis.setMultipleChoiceOptions(question.getMultipleChoiceOptions().stream()
                            .map(option -> new MultipleChoiceOptionDTO(option.getId(), option.getOptionText()))
                            .collect(Collectors.toList()));
                    analysis.setRatingScale(question.getRatingScale());

                    switch (question.getQuestionType()) {
                        case TRUE_FALSE:
                            analyzeTrueFalseAnswers(question, answers, analysis);
                            break;
                        case MULTIPLE_CHOICE:
                            analyzeMultipleChoiceAnswers(question, answers, analysis);
                            break;
                        case RATING_SCALE:
                            analyzeRatingScaleAnswers(question, answers, analysis);
                            break;
                        case TEXT:
                            analyzeTextAnswers(question, answers, analysis);
                            break;
                        default:
                            break;
                    }

                    return analysis;
                })
                .collect(Collectors.toList());

        AnswerAnalysisDTO analysisDTO = new AnswerAnalysisDTO();
        analysisDTO.setQuestionAnalysis(questionAnalysis);

        return analysisDTO;
    }

    private void analyzeTrueFalseAnswers(FormQuestion question, List<Answer> answers, QuestionAnalysisDTO analysis) {
        Long trueCount = answers.stream()
                .filter(answer -> answer.getQuestion().equals(question) && answer.getAnswerText() != null && answer.getAnswerText().equalsIgnoreCase("true"))
                .count();
        Long falseCount = answers.stream()
                .filter(answer -> answer.getQuestion().equals(question) && answer.getAnswerText() != null && answer.getAnswerText().equalsIgnoreCase("false"))
                .count();
        analysis.setTrueCount(trueCount);
        analysis.setFalseCount(falseCount);
    }

    private void analyzeMultipleChoiceAnswers(FormQuestion question, List<Answer> answers, QuestionAnalysisDTO analysis) {
        Map<String, Long> optionSelectionCount = new HashMap<>();
        answers.stream()
                .filter(answer -> answer.getQuestion().equals(question) && answer.getSelectedOptions() != null)
                .flatMap(answer -> answer.getSelectedOptions().stream())
                .forEach(option -> {
                    String optionId = String.valueOf(option.getId());
                    optionSelectionCount.put(optionId, optionSelectionCount.getOrDefault(optionId, 0L) + 1);
                });
        analysis.setOptionSelectionCount(optionSelectionCount);
    }

    private void analyzeRatingScaleAnswers(FormQuestion question, List<Answer> answers, QuestionAnalysisDTO analysis) {
        List<Integer> ratings = answers.stream()
                .filter(answer -> answer.getQuestion().equals(question) && answer.getRating() != null)
                .map(Answer::getRating)
                .collect(Collectors.toList());
        if (!ratings.isEmpty()) {
            double averageRating = ratings.stream()
                    .mapToInt(Integer::intValue)
                    .average()
                    .orElse(0);
            analysis.setAverageRating(averageRating);
        }
    }

    private void analyzeTextAnswers(FormQuestion question, List<Answer> answers, QuestionAnalysisDTO analysis) {
        Map<Long, String> textAnswersWithResponseId = answers.stream()
                .filter(answer -> answer.getQuestion().equals(question) && answer.getAnswerText() != null)
                .collect(Collectors.toMap(
                        answer -> answer.getUserResponse().getId(),
                        Answer::getAnswerText
                ));
        analysis.setTextAnswersWithResponseId(textAnswersWithResponseId);
    }
}