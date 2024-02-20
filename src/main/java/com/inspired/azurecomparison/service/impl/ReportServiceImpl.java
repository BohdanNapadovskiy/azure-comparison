package com.inspired.azurecomparison.service.impl;

import com.inspired.azurecomparison.domain.FileDifference;
import com.inspired.azurecomparison.service.ReportService;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.opencsv.CSVWriter;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ReportServiceImpl implements ReportService {

    private static final String PATTERN_FORMAT = "yyyy-MM-dd";

    @Override
    public void createCSVReport(List<FileDifference> differences, String reportDirectory, String fileName) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(PATTERN_FORMAT)
                .withZone(ZoneId.systemDefault());
        String formattedInstant = formatter.format(Instant.now());
        String fileFolder = reportDirectory + File.separator + formattedInstant;
        try {
            Files.createDirectories(Paths.get(fileFolder));
            File file = new File(fileFolder, FilenameUtils.getBaseName(fileName) + ".csv");
            FileWriter outputfile = new FileWriter(file);
            CSVWriter writer = new CSVWriter(outputfile);
            List<String[]> data = new ArrayList<>();
            data.add(new String[]{"Row", "File Data", "Database data"});
            differences.forEach(d -> data.add(
                    new String[]{
                            String.valueOf(d.getLineNumber()),
                            d.getFileData(),
                            d.getDatabaseData()
                    }
            ));
            writer.writeAll(data);
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();

        }
    }
}
