package com.inspired.azurecomparison.enums;

import org.apache.commons.io.FilenameUtils;

import java.util.Arrays;

public enum FileType {

    PARQUET,
    CSV,
    TXT,
    DELTA;


    public static FileType getFileTypeByFileName(String fileName) {
        String extension = FilenameUtils.getExtension(fileName);
        return Arrays.stream(FileType.values()).toList()
                .stream().filter(t-> t.toString().equalsIgnoreCase(extension))
                .findAny()
                .orElseThrow();
    }


}
