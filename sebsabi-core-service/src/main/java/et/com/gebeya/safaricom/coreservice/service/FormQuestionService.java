package et.com.gebeya.safaricom.coreservice.service;

import et.com.gebeya.safaricom.coreservice.model.FormQuestion;
import et.com.gebeya.safaricom.coreservice.repository.FormQuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FormQuestionService {
    private final FormQuestionRepository formQuestionRepository;

    public List<FormQuestion> getFormQuestionBYFOrmID(Long formId){
        return formQuestionRepository.getFormQuestionByForm_Id(formId);
    }

}
