package et.com.gebeya.safaricom.coreservice.controller;

import et.com.gebeya.safaricom.coreservice.dto.requestDto.FormDto;
import et.com.gebeya.safaricom.coreservice.dto.requestDto.AssignRequest;
import et.com.gebeya.safaricom.coreservice.dto.requestDto.FormQuestionDto;
import et.com.gebeya.safaricom.coreservice.dto.responseDto.JobFormDisplaydto;
import et.com.gebeya.safaricom.coreservice.dto.requestDto.UserResponseRequestDto;
import et.com.gebeya.safaricom.coreservice.model.*;
import et.com.gebeya.safaricom.coreservice.service.FormQuestionService;
import et.com.gebeya.safaricom.coreservice.service.FormService;
import et.com.gebeya.safaricom.coreservice.service.GigWorkerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.InvocationTargetException;
import java.nio.file.AccessDeniedException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/core/forms")
public class FormController {

    private final FormService formService;
    private final FormQuestionService formQuestionService;
    private final GigWorkerService gigWorkerService;

    @PostMapping("/create")
    @ResponseStatus(HttpStatus.CREATED)
    public JobFormDisplaydto createForm(@RequestBody FormDto formDTO ,@RequestParam Long clientId)  {

        JobFormDisplaydto newForm=formService.createForm(formDTO,clientId);
       return newForm;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Form> getAllForms() {
        return formService.getAllForms();
    }
//    @GetMapping("/view")
//    @ResponseStatus(HttpStatus.OK)
//    public List<Form> getAllFormsByStatus(@RequestParam Status status) {
//        return formService.getFormsByStatus(status);
//    }

//    @GetMapping("/view/form")
//    @ResponseStatus(HttpStatus.OK)
//    public List<Form> getAllFormByClientIdAndStatus(@RequestParam Long useriId,@RequestParam Status status) {
//        return formService.getFormsByClientIdAndStatus(useriId,status);
//    }

//    @GetMapping("/{client_id}")
//    @ResponseStatus(HttpStatus.OK)
//    public Optional<Form> getFormByClientId(@PathVariable Long client_id) {
//        return formService.getFormByClientId(client_id);
//    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Form updateForm(@PathVariable Long id, @RequestBody FormDto formDTO) throws InvocationTargetException, IllegalAccessException {
        return formService.updateForm(id, formDTO);
    }
    @PostMapping("/add/question-to-form")
    public Form addQuestionsToForm(@RequestParam Long formID, @RequestBody List<FormQuestionDto> questionDTOList) {
        return formService.addQuestionsToForm(formID, questionDTOList);
    }


    @GetMapping("/view/questionOfForm")
    public List<FormQuestion> viewQuestions(@RequestParam Long formID,Long gigworkerId) throws InvocationTargetException, IllegalAccessException, AccessDeniedException {
        return formQuestionService.getFormQuestionBYFOrmID(formID,gigworkerId);
    }
    @PostMapping("/assign-job")
    public ResponseEntity<GigWorker> assignJobToGigWorker(@RequestBody AssignRequest request) {
        Long gigWorkerId = request.getGigWorkerId();
        Long formId = request.getFormId();
        GigWorker assignedGigWorker = gigWorkerService.assignJobToGigWorker(gigWorkerId, formId);
        return ResponseEntity.ok(assignedGigWorker);
    }

    @GetMapping("/{formId}")
    public ResponseEntity<Form> getForm(@PathVariable Long formId)  {
        Form form = formService.getFormById(formId);
        return ResponseEntity.ok(form);
    }
//    @PostMapping("/submit-response")
//    @ResponseStatus(HttpStatus.CREATED)
//    public UserResponse submitResponse(@RequestBody UserResponseRequestDto responseDTO) {
//        return formService.submitResponse(responseDTO);
//    }
}
