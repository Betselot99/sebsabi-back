package et.com.gebeya.safaricom.coreservice.service;

import et.com.gebeya.safaricom.coreservice.model.Form;
import et.com.gebeya.safaricom.coreservice.model.FormQuestion;
import et.com.gebeya.safaricom.coreservice.repository.FormQuestionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.util.List;

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

}
