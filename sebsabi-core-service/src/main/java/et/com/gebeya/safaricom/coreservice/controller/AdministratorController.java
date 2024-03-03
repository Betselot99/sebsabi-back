package et.com.gebeya.safaricom.coreservice.controller;

import et.com.gebeya.safaricom.coreservice.dto.responseDto.ClientResponse;
import et.com.gebeya.safaricom.coreservice.dto.responseDto.GigwWorkerResponse;
import et.com.gebeya.safaricom.coreservice.model.Client;
import et.com.gebeya.safaricom.coreservice.model.Form;
import et.com.gebeya.safaricom.coreservice.model.Status;
import et.com.gebeya.safaricom.coreservice.service.ClientService;
import et.com.gebeya.safaricom.coreservice.service.FormService;
import et.com.gebeya.safaricom.coreservice.service.GigWorkerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/core/admin/view")
public class AdministratorController {
    private final ClientService clientService;
    private final GigWorkerService gigWorkerService;
    private final FormService formService;

    @GetMapping("/clients")
    public List<ClientResponse> getAllClient(){

        return clientService.getAllClients();
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
    public List<Object[]> countFormsByStatus() {
        return formService.countFormsByStatus();
    }

    @GetMapping("/formsPerClient")
    public List<Object[]> countFormsPerClient() {
        return formService.countFormsPerClient();
    }
    @GetMapping("/proposalsPerForm")
    public List<Object[]> countProposalsPerForm() {
        return formService.countProposalsPerForm();
    }
    @GetMapping("/formsAssignedToGigWorkers")
    public List<Object[]> countFormsAssignedToGigWorkers() {
        return formService.countFormsAssignedToGigWorkers();
    }
    @GetMapping("/countByCompanyType")
    public List<Object[]> countClientsByCompanyType() {
        return clientService.countClientsByCompanyType();
    }
    @GetMapping("/formsPerClient/{status}")
    public List<Object[]> countFormsPerClientByStatus(@PathVariable("status") String status) {
        Status enumStatus = Status.valueOf(status);
        return formService.countFormsPerClientByStatus(enumStatus);
    }
}
