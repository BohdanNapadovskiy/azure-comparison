package com.inspired.azurecomparison.service.impl;

import com.inspired.azurecomparison.domain.FileDifference;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.time.Instant;
import java.util.Iterator;
import java.util.List;

@Service
public class ExcelComparisonService {





    public String extractNameWithoutExtension(String filename) {
        return filename.replaceAll("\\.\\w+$", "");
    }

    public String saveMultipartFileAndGetFile(MultipartFile multipartFile) {
        String formattedInstant = String.valueOf(Instant.now().getEpochSecond());
        File file = new File(formattedInstant + multipartFile.getOriginalFilename());

        try (OutputStream os = new FileOutputStream(file)) {
            os.write(multipartFile.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return file.getAbsolutePath();
    }

    public void compareRowsWithDatabase(Sheet sheet, List<String> databaseData, List<FileDifference> differences) {
        Iterator<Row> rowIterator = sheet.iterator();
        int listIndex = 0;
        int lineNumber = 1;

        while (rowIterator.hasNext() || listIndex < databaseData.size()) {
            String fileLine = rowIterator.hasNext() ? excelRowToString(rowIterator.next()) : "";
            String dbLine = listIndex < databaseData.size() ? databaseData.get(listIndex) : "";

            if (!fileLine.equals(dbLine)) {
                differences.add(FileDifference.buildFileDifference(lineNumber, fileLine, dbLine));
            }

            lineNumber++;
            if (rowIterator.hasNext() || fileLine.isEmpty()) {
                listIndex++;
            }
        }
    }


    private String excelRowToString(Row row) {
        StringBuilder result = new StringBuilder();
        row.forEach(cell -> result.append(createStringFromCellValue(cell)).append(","));
        return result.length() > 0 ? result.substring(0, result.length() - 1) : result.toString();
    }

    private String createStringFromCellValue(Cell cell) {
        DataFormatter formatter = new DataFormatter();
        return formatter.formatCellValue(cell);
    }

}
