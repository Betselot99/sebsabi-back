package et.com.gebeya.safaricom.coreservice.service;

import et.com.gebeya.safaricom.coreservice.Exceptions.FormNotFoundException;
import et.com.gebeya.safaricom.coreservice.dto.requestDto.FormQuestionDto;
import et.com.gebeya.safaricom.coreservice.model.Form;
import et.com.gebeya.safaricom.coreservice.model.FormQuestion;
import et.com.gebeya.safaricom.coreservice.model.MultipleChoiceOption;
import et.com.gebeya.safaricom.coreservice.model.enums.QuestionType;
import et.com.gebeya.safaricom.coreservice.repository.FormQuestionRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.springframework.beans.BeanUtils;

import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class FormQuestionService {
    private final FormQuestionRepository formQuestionRepository;
    private final FormService formService;

    public List<FormQuestion> getFormQuestionBYFOrmID(Long formId,Long gigworkerId) throws AccessDeniedException {
            Form form=formService.getFormForGigWorker(formId,gigworkerId);
        log.info(form.getAssignedGigWorker().getId().toString());
                if(form!=null){
                    return formQuestionRepository.getFormQuestionByForm_Id(form.getId());

                }
                throw new RuntimeException("Dont have access to form");
    }



    public List<FormQuestion> updateFormQuestions(Long formId, List<FormQuestionDto> formQuestionDtoList) {
        List<FormQuestion> updatedQuestions = new ArrayList<>();

        for (FormQuestionDto formQuestionDto : formQuestionDtoList) {
            Long questionId = formQuestionDto.getId();
            Optional<FormQuestion> optionalQuestion = formQuestionRepository.findById(questionId);
            if (optionalQuestion.isPresent()) {
                FormQuestion existingQuestion = optionalQuestion.get();
                if (!existingQuestion.getForm().getId().equals(formId)) {
                    throw new FormNotFoundException("Form question with ID " + questionId + " does not belong to form with ID " + formId);
                }

                try {
                    NullAwareBeanUtilsBean beanUtils = new NullAwareBeanUtilsBean();
                    beanUtils.copyProperties(existingQuestion, formQuestionDto);

                    // Update multiple choice option if necessary
                    if (existingQuestion.getQuestionType() == QuestionType.MULTIPLE_CHOICE) {
                        updateMultipleChoiceOption(existingQuestion, formQuestionDto);
                    }

                    updatedQuestions.add(formQuestionRepository.save(existingQuestion));
                } catch (IllegalAccessException | InvocationTargetException e) {
                    // Handle exception
                    e.printStackTrace();
                    throw new RuntimeException("Error while updating form question");
                }
            } else {
                throw new FormNotFoundException("Form question not found with ID: " + questionId);
            }
        }

        return updatedQuestions;
    }
    private void updateMultipleChoiceOption(FormQuestion existingQuestion, FormQuestionDto formQuestionDto) {
        List<MultipleChoiceOption> existingOptions = existingQuestion.getMultipleChoiceOptions();
        String optionToUpdate = formQuestionDto.getOptionToUpdate();
        String updatedOptionValue = formQuestionDto.getUpdatedOption();

        if (existingOptions == null || existingOptions.isEmpty()) {
            throw new IllegalArgumentException("Invalid option update for multiple choice question");
        }

        // Find the option to update by its value
        Optional<MultipleChoiceOption> optionalOptionToUpdate = existingOptions.stream()
                .filter(option -> option.getOptionText().equals(optionToUpdate))
                .findFirst();

        if (!optionalOptionToUpdate.isPresent()) {
            throw new IllegalArgumentException("Option '" + optionToUpdate + "' not found in the question options");
        }

        // Update the option value
        MultipleChoiceOption optionToUpdateObject = optionalOptionToUpdate.get();
        optionToUpdateObject.setOptionText(updatedOptionValue);
    }

    public static class NullAwareBeanUtilsBean extends BeanUtilsBean {
        @Override
        public void copyProperty(Object dest, String name, Object value) throws IllegalAccessException, InvocationTargetException {
            if (value != null) {
                super.copyProperty(dest, name, value);
            }
        }
    }
    public Optional<FormQuestion> getFormQuestionByFormId(Long formId) {
        return formQuestionRepository.findById(formId);
    }


}
