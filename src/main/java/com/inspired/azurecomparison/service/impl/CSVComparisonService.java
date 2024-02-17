package com.inspired.azurecomparison.service.impl;


import com.inspired.azurecomparison.domain.FileDifference;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import com.opencsv.CSVReader;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CSVComparisonService {

    private final DataDynamicService dataDynamicService;
    private static final Logger logger = LoggerFactory.getLogger(CSVComparisonService.class);


    public List<FileDifference> comparingFileDataWithDataBaseData(MultipartFile file) {
        String nameWithoutExtension = file.getOriginalFilename().replaceAll("\\.\\w+$", "");
        List<String> databaseResult = dataDynamicService.getAllData(nameWithoutExtension);
        return compareDataFromFileAndDataBase(file, databaseResult);
    }


    private List<FileDifference> compareDataFromFileAndDataBase(MultipartFile file, List<String> databaseData) {
        logger.debug("Creating a result of comparison from csv file {} and database", file.getOriginalFilename());
        List<FileDifference> result =new ArrayList<>();
        try (Reader reader = new InputStreamReader(file.getInputStream());
             CSVReader csvData = new CSVReaderBuilder(reader).withSkipLines(1).build()) {
            result =  generateCSVFileReport(csvData, databaseData);
        } catch (CsvValidationException | IOException exception) {
            exception.printStackTrace();
        }
        return result;
    }

    private List<FileDifference> generateCSVFileReport(CSVReader csvData, List<String> databaseData) throws CsvValidationException, IOException {
        List<FileDifference> result =new ArrayList<>();
        String[] line;
        int lineNumber = 1;
        int listIndex = 0;

        while ((line = csvData.readNext()) != null && listIndex < databaseData.size()) {
            String listLine = databaseData.get(listIndex);
            logger.debug("Found difference in data in row number {}, file data {} data from db {} ", lineNumber, arrayToString(line), listLine);
            if (!arraysEqual(line, listLine.split(",")))
                result.add(FileDifference.buildFileDifference(lineNumber, arrayToString(line), listLine));
            lineNumber++;
            listIndex++;
        }

        while ((line = csvData.readNext()) != null) {
            result.add(FileDifference.buildFileDifference(lineNumber, arrayToString(line), ""));
            lineNumber++;
        }

        while (listIndex < databaseData.size()) {
            result.add(FileDifference.buildFileDifference(lineNumber, "", databaseData.get(listIndex)));
            listIndex++;
        }
        return result;
    }

    private static boolean arraysEqual(String[] arr1, String[] arr2) {
        return arr1.length == arr2.length && java.util.Arrays.equals(arr1, arr2);
    }

    private static String arrayToString(String[] arr) {
        return String.join(", ", arr);
    }
}
