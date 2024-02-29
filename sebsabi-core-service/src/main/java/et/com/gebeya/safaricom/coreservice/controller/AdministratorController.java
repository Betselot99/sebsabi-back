package et.com.gebeya.safaricom.coreservice.controller;

import et.com.gebeya.safaricom.coreservice.dto.responseDto.ClientResponse;
import et.com.gebeya.safaricom.coreservice.dto.responseDto.GigwWorkerResponse;
import et.com.gebeya.safaricom.coreservice.service.ClientService;
import et.com.gebeya.safaricom.coreservice.service.FormService;
import et.com.gebeya.safaricom.coreservice.service.GigWorkerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/core/admin")
public class AdministratorController {
    private final ClientService clientService;
    private final GigWorkerService gigWorkerService;
    private final FormService formService;
    @GetMapping("/view/clients")
    public List<ClientResponse> getAllClient(){

        return clientService.getAllClients();
    }
    @GetMapping("/view/number_of_clients")
    public long getNumberOfClients(){
        return clientService.getNumberOfClients();
    }
    @GetMapping("/view/gigworkers")
    public List<GigwWorkerResponse> getAllGigWorkers(){
        return gigWorkerService.getAllGigWorker();
    }
    @GetMapping("/view/number_of_gigworkers")
    public long getNumberofGigWorkers(){
        return gigWorkerService.getNumberofGigWokers();
    }
    @GetMapping("/view/number_of_jobs")
    public long getPostedJobs(){
        return formService.getNumberOfJobs();
    }
    @GetMapping("/view/active_jobs")
    public long getActiveJobs(){
        return formService.getActiveNumberOfJobs();
    }
}
