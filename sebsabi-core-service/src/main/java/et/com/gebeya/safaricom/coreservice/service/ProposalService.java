package et.com.gebeya.safaricom.coreservice.service;


import et.com.gebeya.safaricom.coreservice.dto.requestDto.ProposalDto;
import et.com.gebeya.safaricom.coreservice.dto.responseDto.FormGigworkerDto;
import et.com.gebeya.safaricom.coreservice.model.Form;
import et.com.gebeya.safaricom.coreservice.model.GigWorker;
import et.com.gebeya.safaricom.coreservice.model.Proposal;
import et.com.gebeya.safaricom.coreservice.model.enums.Status;
import et.com.gebeya.safaricom.coreservice.repository.FormRepository;
import et.com.gebeya.safaricom.coreservice.repository.ProposalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

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
        form.setStatus(Status.Claimed);
        GigWorker gigWorker = proposal.getGigWorker();

        form.setAssignedGigWorker(gigWorker);
        formRepository.save(form);

    }
    public List<FormGigworkerDto> findFormsByGigWorkerId(Long gigWorkerId) {
        List<Proposal> proposals = proposalRepository.findByGigWorkerId(gigWorkerId);

        // Extract forms from proposals where form status is not "CLAIMED"
        return proposals.stream()
                .filter(proposal -> !proposal.getForm().getStatus().equals(Status.Claimed))
                .filter(proposal -> proposal.getGigWorker().getId().equals(gigWorkerId))
                .map(proposal -> {
                    Form form = proposal.getForm();
                    return FormGigworkerDto.builder()
                            .id(form.getId())
                            .title(form.getTitle())
                            .description(form.getDescription())
                            .usageLimit(form.getUsageLimit())
                            .proposals(form.getProposals()) // Assuming you have a method to get proposals in Form entity
                            .build();
                })
                .distinct() // Optional: If you want unique forms only
                .collect(Collectors.toList());
    }
    public Proposal findProposalByFormIdAndGigWorkerId(Long formId,Long gigWorkerId) {
        return proposalRepository.findProposalByFormIdAndGigWorkerId(formId,gigWorkerId);
    }



}
