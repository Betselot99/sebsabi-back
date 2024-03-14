package et.com.gebeya.safaricom.coreservice.util.constants.SecurityConstants;

import et.com.gebeya.safaricom.coreservice.dto.responseDto.AnswerAnalysisDTO;
import et.com.gebeya.safaricom.coreservice.dto.responseDto.OptionSelectionCountDTO;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.IOException;
import java.util.Map;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;
@Component
public class ApachePOUUtils {


    public byte[] generateExcelReport(AnswerAnalysisDTO analysisDTO) throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Analysis Report");
            int rowNum = 0;
            Row headerRow = sheet.createRow(rowNum++);
            headerRow.createCell(0).setCellValue("Question");
            headerRow.createCell(1).setCellValue("Option");
            headerRow.createCell(2).setCellValue("Count");

            for (Map.Entry<String, OptionSelectionCountDTO> entry : analysisDTO.getOptionSelectionCount().entrySet()) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(entry.getValue().getOptionId());
                row.createCell(1).setCellValue(entry.getValue().getCount());
            }

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            workbook.write(bos);
            return bos.toByteArray();
        }
    }
}
