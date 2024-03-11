package et.com.gebeya.safaricom.coreservice.controller;

import et.com.gebeya.safaricom.coreservice.dto.analysisDto.*;
import et.com.gebeya.safaricom.coreservice.dto.requestDto.*;
import et.com.gebeya.safaricom.coreservice.dto.responseDto.ClientResponse;
import et.com.gebeya.safaricom.coreservice.dto.responseDto.GigwWorkerResponse;
import et.com.gebeya.safaricom.coreservice.model.Client;
import et.com.gebeya.safaricom.coreservice.model.Form;
import et.com.gebeya.safaricom.coreservice.model.GigWorker;
import et.com.gebeya.safaricom.coreservice.model.Status;
import et.com.gebeya.safaricom.coreservice.service.ClientService;
import et.com.gebeya.safaricom.coreservice.service.FormService;
import et.com.gebeya.safaricom.coreservice.service.GigWorkerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/core/admin/view")
public class AdministratorController {
    private final ClientService clientService;
    private final GigWorkerService gigWorkerService;
    private final FormService formService;

    @GetMapping("/clients")
    public List<ClientResponse> getAllClient(){return clientService.getAllClients();}
    @GetMapping("/search/clients")
    public ResponseEntity<Page<Client>> searchClients(@RequestParam Map<String, String> requestParams,
                                                      @PageableDefault(size = 10) Pageable pageable) {

        ClientSearchRequestDto searchRequestDto = new ClientSearchRequestDto(requestParams);
        Page<Client> clients = clientService.searchClients(searchRequestDto, pageable);
        return new ResponseEntity<>(clients, HttpStatus.OK);
    }
    @PutMapping("/ban/clients")
    @ResponseStatus(HttpStatus.OK)
    public ClientResponse updateForm(@RequestParam long clientId,@RequestBody ClientRequest clientRequest) throws InvocationTargetException, IllegalAccessException {
        if(clientRequest.getIsActive()!=null){
            return clientService.updateClient(clientId, clientRequest);
        }
        throw new RuntimeException("Your not authorized to make other changes");
    }
    @GetMapping("/search")
    public ResponseEntity<Page<Form>> searchForms(
            @RequestParam Map<String, String> requestParams,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        FormSearchRequestDto searchRequestDto = new FormSearchRequestDto(requestParams);
        Page<Form> forms = formService.searchForms(searchRequestDto, pageable);
        return new ResponseEntity<>(forms, HttpStatus.OK);
    }
    @PutMapping("/ban/gig-workers")
    @ResponseStatus(HttpStatus.OK)
    public GigwWorkerResponse updateForm(@RequestParam long gigWorkerid,@RequestBody GigWorkerRequest gigWorkerRequest) throws InvocationTargetException, IllegalAccessException {
        if(gigWorkerRequest.getIsActive()!=null){
            return gigWorkerService.updateGigworker(gigWorkerid, gigWorkerRequest);
        }
        throw new RuntimeException("Your not authorized to make other changes");
    }
    @GetMapping("/number_of_clients")
    public long getNumberOfClients(){
        return clientService.getNumberOfClients();
    }
    @GetMapping("/gigworkers")
    public List<GigwWorkerResponse> getAllGigWorkers(){
        return gigWorkerService.getAllGigWorker();
    }
    @GetMapping("/AllForms")
    public List<Form> getAllForms(){
        return formService.getAllForms();
    }
    @GetMapping("/number_of_gigworkers")
    public long getNumberofGigWorkers(){
        return gigWorkerService.getNumberofGigWokers();
    }
//    @GetMapping("/view/number_of_jobs/assigned")
//    public long getPostedJobs(){
//        return formService.getNumberOfJobsAssigned();
//    }
    @GetMapping("/countByStatus")
    public List<FormsByStatusDTO> countFormsByStatus() {
        return formService.countFormsByStatus();
    }

    @GetMapping("/formsPerClient")
    public List<FormsPerClientDTO> countFormsPerClient() {
        return formService.countFormsPerClient();
    }
    @GetMapping("/proposalsPerForm")
    public List<ProposalsPerFormDTO> countProposalsPerForm() {
        return formService.countProposalsPerForm();
    }
    @GetMapping("/formsAssignedToGigWorkers")
    public List<FormsAssignedToGigWorkersDTO> countFormsAssignedToGigWorkers() {
        return formService.countFormsAssignedToGigWorkers();
    }
//    @GetMapping("/countByCompanyType")
//    public List<Compa> countClientsByCompanyType() {
//        return clientService.countClientsByCompanyType();
//    }
    @GetMapping("/formsPerClient/{status}")
    public List<FormsPerClientByStatusDTO> countFormsPerClientByStatus(@PathVariable("status") String status) {
        Status enumStatus = Status.valueOf(status);
        return formService.countFormsPerClientByStatus(enumStatus);
    }
}
