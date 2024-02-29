package et.com.gebeya.safaricom.coreservice.controller;

import et.com.gebeya.safaricom.coreservice.dto.responseDto.ClientResponse;
import et.com.gebeya.safaricom.coreservice.model.Client;
import et.com.gebeya.safaricom.coreservice.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/core/admin")
public class AdministratorController {
    private final ClientService clientService;
    @GetMapping("/view/clients")
    public List<ClientResponse> getAllClient(){

        return clientService.getAllClients();
    }

}
