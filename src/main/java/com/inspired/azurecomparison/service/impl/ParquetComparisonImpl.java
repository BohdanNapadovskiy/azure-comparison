package com.inspired.azurecomparison.service.impl;


import com.inspired.azurecomparison.domain.FileDifference;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.apache.hadoop.fs.Path;
import org.apache.parquet.hadoop.example.GroupReadSupport;
import org.apache.parquet.hadoop.ParquetReader;
import org.apache.parquet.example.data.Group;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
public class ParquetComparisonImpl {

    private final DataDynamicService dataDynamicService;
    private static final Logger logger = LoggerFactory.getLogger(CSVComparisonService.class);

    public List<FileDifference> readParquetFile(MultipartFile file) {
        List<FileDifference> result;
        String nameWithoutExtension = FilenameUtils.getBaseName(file.getOriginalFilename());
        Path hadoopFilepath = saveMultipartFileAndGetHadoopPath(file);
        List<String> databaseResult = dataDynamicService.getAllData(nameWithoutExtension);
        GroupReadSupport readSupport = new GroupReadSupport();
        try (ParquetReader<Group> reader = ParquetReader.builder(readSupport, hadoopFilepath).build()) {
            result = generateFileReport(reader, databaseResult);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        deleteTemporaryFile(hadoopFilepath.getName());
        return result;
    }



    private List<FileDifference> generateFileReport(ParquetReader<Group> reader, List<String> databaseData) throws IOException {
        List<FileDifference> result = new ArrayList<>();
        int lineNumber = 1;
        int listIndex = 0;
        Group record;
        while ((record = reader.read()) != null && listIndex < databaseData.size()) {
            String fileLine = groupToString(record);
            String listLine = databaseData.get(listIndex);
            logger.debug("Found difference in data in row number {}, file data {} data from db {} ", lineNumber, fileLine, listLine);
            if (!fileLine.equals(listLine))
                result.add(FileDifference.buildFileDifference(lineNumber, fileLine, listLine));
            lineNumber++;
            listIndex++;
        }

        while ((record = reader.read()) != null) {
            String fileLine = groupToString(record);
            result.add(FileDifference.buildFileDifference(lineNumber, fileLine, ""));
            lineNumber++;
        }

        while (listIndex < databaseData.size()) {
            result.add(FileDifference.buildFileDifference(lineNumber, "", databaseData.get(listIndex)));
            listIndex++;
        }
        return result;
    }

    private String groupToString(Group record) {
        List<String> resultList = new ArrayList<>();
        int numberOfFields = record.getType().getFieldCount();
        for (int i = 0; i < numberOfFields; i++) {
            resultList.add(record.getValueToString(i, 0));
        }
        return String.join(",", resultList);
    }


    public Path saveMultipartFileAndGetHadoopPath(MultipartFile multipartFile) {
        String formattedInstant = String.valueOf(Instant.now().getEpochSecond());
        String filePath = formattedInstant + FilenameUtils.getBaseName(multipartFile.getOriginalFilename())+"."+FilenameUtils.getExtension(multipartFile.getOriginalFilename());
        java.nio.file.Path path = Paths.get(filePath);
        try {
            Files.write(path, multipartFile.getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new Path(filePath);
    }

    private void deleteTemporaryFile(String name) {
        java.nio.file.Path path = Paths.get(name);
        try {
            Files.delete(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

}
