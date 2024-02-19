package com.inspired.azurecomparison.service.impl;

import com.inspired.azurecomparison.domain.FileDifference;
import com.inspired.azurecomparison.service.ReportService;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.opencsv.CSVWriter;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class ReportServiceImpl implements ReportService {


    @Value("${application.reportPath}")
    private String reportDirectory;


    @Override
    public void createCSVReport(List<FileDifference> differences) {

        String formattedInstant = String.valueOf(Instant.now().getEpochSecond());
        String fileName = formattedInstant + "report.csv";
        File file = new File(reportDirectory + fileName);
        try {
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
