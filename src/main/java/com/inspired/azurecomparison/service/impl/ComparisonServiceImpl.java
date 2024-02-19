package com.inspired.azurecomparison.service.impl;

import com.inspired.azurecomparison.domain.FileDifference;
import com.inspired.azurecomparison.enums.FileType;
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
public class ComparisonServiceImpl implements ComparisonService {

    private final ParquetComparisonImpl parquetComparison;
    private final TXTComparisonService txtComparisonService;
    private final CSVComparisonService csvComparisonService;
    private final ExcelXLSComparisonService excelXLSComparisonService;
    private final ExcelXLSXComparisonService excelXLSXComparisonService;
    private static final Logger logger = LoggerFactory.getLogger(ComparisonServiceImpl.class);
    private final Map<FileType, Function<MultipartFile, List<FileDifference>>> dispatch = new HashMap<>();

    @PostConstruct
    public ComparisonServiceImpl init() {
        this.dispatch.put(FileType.CSV, this.compareCSVFile());
        this.dispatch.put(FileType.PARQUET, this.compareParquetFile());
        this.dispatch.put(FileType.XLSX, this.compareExcelXLSXFile());
        this.dispatch.put(FileType.XLS, this.compareExcelXLSFile());
        this.dispatch.put(FileType.TXT, this.compareTXTFile());
        return this;
    }


    @Override
    public List<FileDifference> getResultOfComparisonAccordingFileExtension(MultipartFile file) {
        FileType fileType = FileType.getFileTypeByFileName(FilenameUtils.getExtension(file.getOriginalFilename()));
        if (dispatch.containsKey(fileType)) {
            logger.debug("Starting process for comparing data from csv file {}", file.getOriginalFilename());
            return dispatch.get(fileType).apply(file);
        } else {
            //TODO
            // ned add throw exception if file not supported
            return null;
        }
    }


    private Function<MultipartFile, List<FileDifference>> compareCSVFile() {
        return csvComparisonService::comparingFileDataWithDataBaseData;
    }


    private Function<MultipartFile, List<FileDifference>> compareParquetFile() {
        return parquetComparison::readParquetFile;
    }

    private Function<MultipartFile, List<FileDifference>> compareExcelXLSXFile() {
        return excelXLSXComparisonService::comparingFileDataWithDataBaseData;
    }

    private Function<MultipartFile, List<FileDifference>> compareExcelXLSFile() {
        return excelXLSComparisonService::comparingFileDataWithDataBaseData;
    }

    private Function<MultipartFile, List<FileDifference>> compareTXTFile() {
        return txtComparisonService::comparingFileDataWithDataBaseData;
    }


}
