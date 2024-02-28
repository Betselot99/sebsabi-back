package et.com.gebeya.safaricom.coreservice.service;


import et.com.gebeya.safaricom.coreservice.dto.requestDto.ProposalDto;
import et.com.gebeya.safaricom.coreservice.model.Form;
import et.com.gebeya.safaricom.coreservice.model.GigWorker;
import et.com.gebeya.safaricom.coreservice.model.Proposal;
import et.com.gebeya.safaricom.coreservice.repository.ProposalRepository;
import org.springframework.stereotype.Service;

@Service
public class ProposalService {
    private final ProposalRepository proposalRepository;
    private final FormService formService; // Inject FormService
    private final GigWorkerService gigWorkerService; // Inject GigWorkerService

    public ProposalService(ProposalRepository proposalRepository, FormService formService, GigWorkerService gigWorkerService) {
        this.proposalRepository = proposalRepository;
        this.formService = formService;
        this.gigWorkerService = gigWorkerService;
    }

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

    public ProposalDto getProposalByFormId(Long formId) {
        Proposal proposal = proposalRepository.findByFormId(formId);

        // Ensure the proposal exists
        if (proposal == null) {
            // Handle the case where the proposal is not found, return null or throw an exception
            return null;
        }

        // Retrieve the associated form and gig worker
        Form form = proposal.getForm();
        GigWorker gigWorker = proposal.getGigWorker();

        // Map the entities to DTO
        ProposalDto proposalDto = new ProposalDto();
        proposalDto.setFormId(form.getId());
        proposalDto.setGigWorkerId(gigWorker.getId());
        proposalDto.setRatePerForm(proposal.getRatePerForm());
        proposalDto.setProposalText(proposal.getProposalText());

        return proposalDto;
    }
}
