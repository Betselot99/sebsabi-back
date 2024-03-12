package et.com.gebeya.safaricom.coreservice.service;

import et.com.gebeya.safaricom.coreservice.dto.responseDto.AnswerAnalysisDTO;
import et.com.gebeya.safaricom.coreservice.dto.responseDto.OptionSelectionCountDTO;
import et.com.gebeya.safaricom.coreservice.model.Answer;
import et.com.gebeya.safaricom.coreservice.model.Form;
import et.com.gebeya.safaricom.coreservice.model.MultipleChoiceOption;
import et.com.gebeya.safaricom.coreservice.model.Status;
import et.com.gebeya.safaricom.coreservice.model.enums.QuestionType;
import et.com.gebeya.safaricom.coreservice.repository.AnswerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@RequiredArgsConstructor
@Service
public class AnswerService {
    private final AnswerRepository answerRepository;
    private final FormService formService;

    public Answer saveAnswer(Answer answer) {
        return answerRepository.save(answer);
    }

    public AnswerAnalysisDTO analyzeAnswers(Long formId ,Long client_id) throws AccessDeniedException {
        Form form = formService.getFormForClientByFormId(formId,client_id);
        List<Answer> answers = answerRepository.findByUserResponse_Form_Id(formId);

        if (form.getStatus() != Status.Completed) {
            throw new RuntimeException("Form is not completed yet");
        }

        Map<String, Long> optionSelectionCount = new HashMap<>();
        long trueCount = 0;
        long falseCount = 0;
        AtomicLong rangeTotalSum = new AtomicLong(0); // Use AtomicLong to support concurrent updates
        AtomicLong rangeQuestionCount = new AtomicLong(0); // Use AtomicLong to support concurrent updates
        Map<Long, String> textAnswersWithResponseId = new HashMap<>();

        for (Answer answer : answers) {
            if (answer.getQuestion().getQuestionType().equals(QuestionType.MULTIPLE_CHOICE)) {
                countOptionSelections(answer, optionSelectionCount);
            } else if (answer.getQuestion().getQuestionType().equals(QuestionType.TRUE_FALSE)) {
                calculateTrueFalse(answer, trueCount, falseCount);
            } else if (answer.getQuestion().getQuestionType().equals(QuestionType.RATING_SCALE)) {
                calculateRange(answer, rangeTotalSum, rangeQuestionCount);
            } else if (answer.getQuestion().getQuestionType().equals(QuestionType.TEXT)) {
                textAnswersWithResponseId.put(answer.getUserResponse().getId(), answer.getAnswerText());
            }
        }

        double trueFalseAverage = calculateTrueFalseAverage(trueCount, falseCount, answers.size());
        double rangeAverage = calculateRangeAverage(rangeTotalSum.get(), rangeQuestionCount.get());

        AnswerAnalysisDTO analysisDTO = new AnswerAnalysisDTO();
        analysisDTO.setOptionSelectionCount(mapOptionSelectionCountToDTO(optionSelectionCount));
        analysisDTO.setTrueFalseAverage(trueFalseAverage);
        analysisDTO.setRangeAverage(rangeAverage);
        analysisDTO.setTextAnswersWithResponseId(textAnswersWithResponseId);

        return analysisDTO;
    }

    private void countOptionSelections(Answer answer, Map<String, Long> optionSelectionCount) {
        answer.getSelectedOptions().forEach(option -> {
            String optionId = String.valueOf(option.getId());
            optionSelectionCount.put(optionId, optionSelectionCount.getOrDefault(optionId, 0L) + 1);
        });
    }

    private void calculateTrueFalse(Answer answer, long trueCount, long falseCount) {
        String answerText = answer.getAnswerText();
        if (answerText != null) {
            if (answerText.equalsIgnoreCase("true")) {
                trueCount++;
            } else if (answerText.equalsIgnoreCase("false")) {
                falseCount++;
            }
        }
    }

    private void calculateRange(Answer answer, AtomicLong rangeTotalSum, AtomicLong rangeQuestionCount) {
        Integer rating = answer.getRating();
        if (rating != null) {
            rangeTotalSum.addAndGet(rating);
            rangeQuestionCount.incrementAndGet();
        }
    }



    private double calculateTrueFalseAverage(long trueCount, long falseCount, int totalAnswers) {
        long totalCount = trueCount + falseCount;
        return totalCount == 0 ? 0 : (double) totalCount / totalAnswers;
    }

    private double calculateRangeAverage(long rangeTotalSum, long rangeQuestionCount) {
        return rangeQuestionCount == 0 ? 0 : (double) rangeTotalSum / rangeQuestionCount;
    }

    private Map<String, OptionSelectionCountDTO> mapOptionSelectionCountToDTO(Map<String, Long> optionSelectionCount) {
        Map<String, OptionSelectionCountDTO> optionSelectionCountDTO = new HashMap<>();
        optionSelectionCount.forEach((key, value) -> optionSelectionCountDTO.put(key, new OptionSelectionCountDTO(key, value)));
        return optionSelectionCountDTO;
    }
}
