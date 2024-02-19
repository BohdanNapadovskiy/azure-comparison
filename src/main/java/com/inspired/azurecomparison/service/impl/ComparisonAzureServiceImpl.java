package com.inspired.azurecomparison.service.impl;

import com.inspired.azurecomparison.domain.FileDifference;
import com.inspired.azurecomparison.enums.FileType;
import com.inspired.azurecomparison.service.ComparisonAzureService;
import com.inspired.azurecomparison.service.ComparisonService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class ComparisonAzureServiceImpl implements ComparisonAzureService {

    private final ParquetComparisonImpl parquetComparison;
    private final TXTComparisonService txtComparisonService;
    private final CSVComparisonService csvComparisonService;
    private final ExcelXLSComparisonService excelXLSComparisonService;
    private final ExcelXLSXComparisonService excelXLSXComparisonService;
    private static final Logger logger = LoggerFactory.getLogger(ComparisonAzureServiceImpl.class);
    private final Map<FileType, Function<String, List<FileDifference>>> azureDispatcher = new HashMap<>();


    @PostConstruct
    public ComparisonAzureServiceImpl init() {
        this.azureDispatcher.put(FileType.CSV, this.compareAzureCSVFile());
        this.azureDispatcher.put(FileType.PARQUET, this.compareAzureParquetFile());
        this.azureDispatcher.put(FileType.XLSX, this.compareAzureExcelXLSXFile());
        this.azureDispatcher.put(FileType.XLS, this.compareAzureExcelXLSFile());
        this.azureDispatcher.put(FileType.TXT, this.compareAzureTXTFile());

        return this;
    }


    @Override
    public List<FileDifference> getResultOfComparisonAccordingFileExtension(String filePath) {
        FileType fileType = FileType.getFileTypeByFileName(FilenameUtils.getExtension(filePath));
        if (azureDispatcher.containsKey(fileType)) {
            csvComparisonService.comparingFileDataWithDataBaseData(filePath);
//            logger.debug("Starting process for comparing data from csv file {}", file.getOriginalFilename());
            return azureDispatcher.get(fileType).apply(filePath);
        } else {
            //TODO
            // ned add throw exception if file not supported
            return null;
        }
    }


    private Function<String, List<FileDifference>> compareAzureCSVFile() {
        return csvComparisonService::comparingFileDataWithDataBaseData;
    }

    private Function<String, List<FileDifference>> compareAzureParquetFile() {
        return parquetComparison::readParquetFile;
    }

    private Function<String, List<FileDifference>> compareAzureExcelXLSXFile() {
        return excelXLSXComparisonService::comparingFileDataWithDataBaseData;
    }

    private Function<String, List<FileDifference>> compareAzureExcelXLSFile() {
        return excelXLSComparisonService::comparingFileDataWithDataBaseData;
    }

    private Function<String, List<FileDifference>> compareAzureTXTFile() {
        return txtComparisonService::comparingFileDataWithDataBaseData;
    }

}
