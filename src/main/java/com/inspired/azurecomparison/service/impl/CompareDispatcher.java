package com.inspired.azurecomparison.service.impl;

import com.inspired.azurecomparison.domain.FileDifference;
import com.inspired.azurecomparison.enums.FileType;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

@Service
@RequiredArgsConstructor
public class CompareDispatcher  {

    private final ParquetComparisonImpl parquetComparison;
    private final TXTComparisonService txtComparisonService;
    private final CSVComparisonService csvComparisonService;
    private final ExcelXLSComparisonService excelXLSComparisonService;
    private final ExcelXLSXComparisonService excelXLSXComparisonService;
    private static final Logger logger = LoggerFactory.getLogger(CompareDispatcher.class);
    private final Map<FileType, BiFunction<String, List<String>, List<FileDifference>>> azureDispatcher = new HashMap<>();


    @PostConstruct
    public CompareDispatcher init() {
        this.azureDispatcher.put(FileType.CSV, this.compareAzureCSVFile());
        this.azureDispatcher.put(FileType.PARQUET, this.compareAzureParquetFile());
        this.azureDispatcher.put(FileType.XLSX, this.compareAzureExcelXLSXFile());
        this.azureDispatcher.put(FileType.XLS, this.compareAzureExcelXLSFile());
        this.azureDispatcher.put(FileType.TXT, this.compareAzureTXTFile());
        return this;
    }


    public List<FileDifference> compareDataFromFileAndDataBase(String file, List<String> databaseResult) {
        FileType fileType = FileType.getFileTypeByFileName(FilenameUtils.getExtension(file));
        if (azureDispatcher.containsKey(fileType)) {
//                       logger.debug("Starting process for comparing data from csv file {}", file.getOriginalFilename());
            return azureDispatcher.get(fileType).apply(file, databaseResult);
        } else {
            return null;
        }
    }

    private BiFunction<String, List<String>, List<FileDifference>> compareAzureCSVFile() {
        return (file, databaseResult) -> csvComparisonService.compareDataFromFileAndDataBase(file,databaseResult);
    }


    private BiFunction<String, List<String>, List<FileDifference>> compareAzureParquetFile() {
        return (file, databaseResult) -> parquetComparison.compareDataFromFileAndDataBase(file,databaseResult);
    }

    private BiFunction<String, List<String>, List<FileDifference>> compareAzureExcelXLSXFile() {
        return (file, databaseResult) -> excelXLSXComparisonService.compareDataFromFileAndDataBase(file,databaseResult);
    }

    private BiFunction<String, List<String>, List<FileDifference>> compareAzureExcelXLSFile() {
        return (file, databaseResult) -> excelXLSComparisonService.compareDataFromFileAndDataBase(file,databaseResult);
    }

    private BiFunction<String, List<String>, List<FileDifference>> compareAzureTXTFile() {
        return (file, databaseResult) -> txtComparisonService.compareDataFromFileAndDataBase(file,databaseResult);
    }

}
