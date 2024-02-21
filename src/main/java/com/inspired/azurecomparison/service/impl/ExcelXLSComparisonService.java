package com.inspired.azurecomparison.service.impl;

import com.inspired.azurecomparison.service.db.DataDynamicService;
import com.inspired.azurecomparison.domain.FileDifference;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
public class ExcelXLSComparisonService extends ExcelComparisonService {

    private static final Logger logger = LoggerFactory.getLogger(CSVComparisonService.class);

    public List<FileDifference> compareDataFromFileAndDataBase(String downloadedFile, List<String> databaseData) {
        List<FileDifference> differences = new ArrayList<>();
        FileInputStream file = null;
        try {
            file = new FileInputStream(downloadedFile);
            HSSFWorkbook workbook = new HSSFWorkbook(file);
            HSSFSheet sheet = workbook.getSheetAt(0);
            compareRowsWithDatabase(sheet, databaseData, differences);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return differences;
    }




}
