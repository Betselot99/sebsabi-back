package et.com.gebeya.safaricom.coreservice.service;

import et.com.gebeya.safaricom.coreservice.dto.responseDto.AnswerAnalysisDTO;
import et.com.gebeya.safaricom.coreservice.util.constants.SecurityConstants.ApachePOUUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.AccessDeniedException;

@Service
public class AnswerAnalysisService {

    private final AnswerService answerService;

    private final ApachePOUUtils apachePOIUtils;

    @Autowired
    public AnswerAnalysisService(AnswerService answerService, ApachePOUUtils apachePOIUtils) {
        this.answerService = answerService;
        this.apachePOIUtils = apachePOIUtils;
    }

    public AnswerAnalysisDTO analyzeAnswers(Long formId, Long clientId) throws IOException {
        // Perform analysis using existing logic
        AnswerAnalysisDTO analysisDTO = answerService.analyzeAnswers(formId, clientId);

        // Additional analysis using Apache Commons Math

        // Generate Excel report using Apache POI
        byte[] excelReport = apachePOIUtils.generateExcelReport(analysisDTO);
        analysisDTO.setExcelReport(excelReport);

        return analysisDTO;
    }
}

