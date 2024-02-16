package com.inspired.azurecomparison.service.impl;

import com.inspired.azurecomparison.domain.FileDifference;
import com.inspired.azurecomparison.enums.FileType;
import com.inspired.azurecomparison.service.CSVComparisonService;
import com.inspired.azurecomparison.service.ComparisonService;
import com.inspired.azurecomparison.service.TXTFileComparison;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
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

    private final CSVComparisonService csvComparisonService;
    private final TXTFileComparison txtFileComparison;
    private static final Logger logger = LoggerFactory.getLogger(ComparisonServiceImpl.class);
    private final Map<FileType, Function<MultipartFile, List<FileDifference>>> dispatch = new HashMap<>();


    @PostConstruct
    public ComparisonServiceImpl init() {
        this.dispatch.put(FileType.CSV, this.compareCSVFile());
        this.dispatch.put(FileType.TXT, this.compareTXTFile());
        this.dispatch.put(FileType.PARQUET, this.compareParqetFile());
        return this;
    }


    @Override
    public List<FileDifference> getResultOfComparisonAccordingFileExtension(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        FileType fileType = FileType.getFileTypeByFileName(fileName);
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

    private Function<MultipartFile, List<FileDifference>> compareTXTFile() {
        return txtFileComparison::comparingFileDataWithDataBaseData;
    }

    private Function<MultipartFile, List<FileDifference>> compareParqetFile() {
        return fileDifferences -> {
            return null;// s -> ParqetComparisonImpl.compareData(s, null);
        };
    }


}
