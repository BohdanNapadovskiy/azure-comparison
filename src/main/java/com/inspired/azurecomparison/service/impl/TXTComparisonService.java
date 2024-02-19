package com.inspired.azurecomparison.service.impl;

import com.inspired.azurecomparison.domain.FileDifference;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TXTComparisonService {

    private final DataDynamicService dataDynamicService;
    private static final Logger logger = LoggerFactory.getLogger(CSVComparisonService.class);

    public List<FileDifference> comparingFileDataWithDataBaseData(MultipartFile file) {
        String fileName = FilenameUtils.getBaseName(file.getOriginalFilename());
        List<String> databaseResult = dataDynamicService.getAllData(fileName);
        return compareDataFromFileAndDataBase(file, databaseResult);
    }


    public List<FileDifference> comparingFileDataWithDataBaseData(String downloadedFile) {
        String fileName = FilenameUtils.getBaseName(downloadedFile);
        List<String> databaseResult = dataDynamicService.getAllData(fileName);
        return compareDataFromFileAndDataBase(downloadedFile, databaseResult);
    }


    private List<FileDifference> compareDataFromFileAndDataBase(String downloadedFile, List<String> databaseData) {
        logger.debug("Creating a result of comparison from csv file {} and database", downloadedFile);
        List<FileDifference> result =new ArrayList<>();
        int lineNumber = 1;
        int listIndex = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(downloadedFile))) {
            String line;
            while ((line = reader.readLine()) != null && listIndex < databaseData.size()) {
                String listLine = databaseData.get(listIndex);
                logger.debug("Found difference in data in row number {}, file data {} data from db {} ", lineNumber, line, listLine);
                if (!line.equalsIgnoreCase(listLine))
                    result.add(FileDifference.buildFileDifference(lineNumber, line, listLine));
                lineNumber++;
                listIndex++;
            }

            while ((line = reader.readLine()) != null) {
                result.add(FileDifference.buildFileDifference(lineNumber, line, ""));
                lineNumber++;
            }

            while (listIndex < databaseData.size()) {
                result.add(FileDifference.buildFileDifference(lineNumber, "", databaseData.get(listIndex)));
                listIndex++;
            }
            return result;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }



    private List<FileDifference> compareDataFromFileAndDataBase(MultipartFile file, List<String> databaseData) {
        logger.debug("Creating a result of comparison from csv file {} and database", file.getOriginalFilename());
        String filePath = saveMultipartFileAndGetFile(file);
        List<FileDifference> result =new ArrayList<>();
        int lineNumber = 1;
        int listIndex = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null && listIndex < databaseData.size()) {
                String listLine = databaseData.get(listIndex);
                logger.debug("Found difference in data in row number {}, file data {} data from db {} ", lineNumber, line, listLine);
                if (!line.equalsIgnoreCase(listLine))
                    result.add(FileDifference.buildFileDifference(lineNumber, line, listLine));
                lineNumber++;
                listIndex++;
            }

            while ((line = reader.readLine()) != null) {
                result.add(FileDifference.buildFileDifference(lineNumber, line, ""));
                lineNumber++;
            }

            while (listIndex < databaseData.size()) {
                result.add(FileDifference.buildFileDifference(lineNumber, "", databaseData.get(listIndex)));
                listIndex++;
            }
            return result;
        } catch (IOException e) {
            e.printStackTrace();
        }
       return result;
    }

    public String saveMultipartFileAndGetFile(MultipartFile multipartFile) {
        String formattedInstant = String.valueOf(Instant.now().getEpochSecond());
        String filePath = formattedInstant + FilenameUtils.getBaseName(multipartFile.getOriginalFilename())+"."+FilenameUtils.getExtension(multipartFile.getOriginalFilename());
        File file = new File(filePath);
        try (OutputStream os = new FileOutputStream(file)) {
            os.write(multipartFile.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return file.getAbsolutePath();
    }

}
