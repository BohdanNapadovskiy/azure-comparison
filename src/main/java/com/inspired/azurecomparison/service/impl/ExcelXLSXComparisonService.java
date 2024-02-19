package com.inspired.azurecomparison.service.impl;

import com.inspired.azurecomparison.domain.FileDifference;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExcelXLSXComparisonService extends ExcelComparisonService {

    private static final Logger logger = LoggerFactory.getLogger(CSVComparisonService.class);
    private final DataDynamicService dataDynamicService;


    public List<FileDifference> comparingFileDataWithDataBaseData(MultipartFile file) {
        String nameWithoutExtension = FilenameUtils.getBaseName(file.getOriginalFilename());
        List<String> databaseResult = dataDynamicService.getAllData(nameWithoutExtension);
        return compareDataFromFileAndDataBase(file, databaseResult);
    }

    public List<FileDifference> comparingFileDataWithDataBaseData(String downloadedFile) {
        String nameWithoutExtension = FilenameUtils.getBaseName(downloadedFile);
        List<String> databaseResult = dataDynamicService.getAllData(nameWithoutExtension);
        return compareDataFromFileAndDataBase(downloadedFile, databaseResult);
    }


    private List<FileDifference> compareDataFromFileAndDataBase(String downloadedFile, List<String> databaseData) {
        List<FileDifference> differences = new ArrayList<>();
        FileInputStream file = null;
        try {
            file = new FileInputStream(downloadedFile);
            XSSFWorkbook workbook = new XSSFWorkbook(file);
            XSSFSheet sheet = workbook.getSheetAt(0);
            compareRowsWithDatabase(sheet, databaseData, differences);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return differences;
    }



    private List<FileDifference> compareDataFromFileAndDataBase(MultipartFile multipartFile, List<String> databaseData) {
        List<FileDifference> differences = new ArrayList<>();
        FileInputStream file = null;
        String temporaryFile = saveMultipartFileAndGetFile(multipartFile);
        try {
            file = new FileInputStream(temporaryFile);
            XSSFWorkbook workbook = new XSSFWorkbook(file);
            XSSFSheet sheet = workbook.getSheetAt(0);
            compareRowsWithDatabase(sheet, databaseData, differences);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return differences;
    }


}

