package com.inspired.azurecomparison.service.impl;

import com.inspired.azurecomparison.domain.FileDifference;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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
    private final DataDynamicService dataDynamicService;

    public List<FileDifference> comparingFileDataWithDataBaseData(MultipartFile file) {
        String nameWithoutExtension = FilenameUtils.getBaseName((file.getOriginalFilename()));
        List<String> databaseResult = dataDynamicService.getAllData(nameWithoutExtension);
        return compareDataFromFileAndDataBase(file, databaseResult);
    }


    private List<FileDifference> compareDataFromFileAndDataBase(MultipartFile multipartFile, List<String> databaseData) {
        List<FileDifference> differences = new ArrayList<>();
        FileInputStream file = null;
        String temporaryFile = saveMultipartFileAndGetFile(multipartFile);
        try {
            file = new FileInputStream(temporaryFile);
            HSSFWorkbook workbook = new HSSFWorkbook(file);
            HSSFSheet sheet = workbook.getSheetAt(0);
            compareRowsWithDatabase(sheet, databaseData, differences);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return differences;
    }




}
