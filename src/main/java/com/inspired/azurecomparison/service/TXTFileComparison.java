package com.inspired.azurecomparison.service;

import com.inspired.azurecomparison.domain.FileDifference;
import com.inspired.azurecomparison.service.impl.DataDynamicService;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.exceptions.CsvValidationException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TXTFileComparison {

    private final DataDynamicService dataDynamicService;
    private static final Logger logger = LoggerFactory.getLogger(CSVComparisonService.class);

    public List<FileDifference> comparingFileDataWithDataBaseData(MultipartFile file) {
        String nameWithoutExtension = file.getOriginalFilename().replaceAll("\\.\\w+$", "");
        List<String> databaseResult = dataDynamicService.getAllData(nameWithoutExtension);
        return compareDataFromFileAndDataBase(file, databaseResult);
    }


    private List<FileDifference> compareDataFromFileAndDataBase(MultipartFile file, List<String> databaseData) {
        logger.debug("Creating a result of comparison from txt file {} and database", file.getOriginalFilename());
        List<FileDifference> result =new ArrayList<>();
        BufferedReader br;
        try {
            String line;
            InputStream is = file.getInputStream();
            br = new BufferedReader(new InputStreamReader(is));
            while ((line = br.readLine()) != null) {
//                result.add(line);
            }

        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        return result;
    }



}
