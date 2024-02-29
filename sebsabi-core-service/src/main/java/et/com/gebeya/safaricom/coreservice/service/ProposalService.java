package et.com.gebeya.safaricom.coreservice.service;


import et.com.gebeya.safaricom.coreservice.dto.requestDto.ProposalDto;
import et.com.gebeya.safaricom.coreservice.model.Form;
import et.com.gebeya.safaricom.coreservice.model.GigWorker;
import et.com.gebeya.safaricom.coreservice.model.Proposal;
import et.com.gebeya.safaricom.coreservice.repository.FormRepository;
import et.com.gebeya.safaricom.coreservice.repository.ProposalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProposalService {
    private final ProposalRepository proposalRepository;
    private final FormService formService; // Inject FormService
    private final GigWorkerService gigWorkerService; // Inject GigWorkerService
    private final FormRepository formRepository; // Inject GigWorkerService




    public void submitProposal(ProposalDto proposalDto) {
        Form form = formService.getFormById(proposalDto.getFormId());
        GigWorker gigWorker = gigWorkerService.getGigWorkerByIdg(proposalDto.getGigWorkerId());

        Proposal proposal = new Proposal();
        proposal.setForm(form);
        proposal.setGigWorker(gigWorker);
        proposal.setRatePerForm(proposalDto.getRatePerForm());
        proposal.setProposalText(proposalDto.getProposalText());
        proposalRepository.save(proposal);
    }

    public List<Proposal> getProposalsByFormId(Long formId) {
        return proposalRepository.findByFormId(formId);
    }

    public void acceptProposal(Long proposalId) {
        Proposal proposal = proposalRepository.findById(proposalId)
                .orElseThrow(() -> new RuntimeException("Proposal not found with id: " + proposalId));

        // Update proposal status or perform other actions as needed

        // Assign form to gig worker
        Form form = proposal.getForm();
        GigWorker gigWorker = proposal.getGigWorker();

        form.setAssignedGigWorker(gigWorker);
        formRepository.save(form);

    }
}
