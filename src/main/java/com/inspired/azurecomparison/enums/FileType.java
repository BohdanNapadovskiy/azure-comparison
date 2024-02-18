package com.inspired.azurecomparison.enums;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.util.Arrays;
import java.util.stream.Stream;

public enum FileType {

    PARQUET,
    CSV,
    XLSX,
    TXT,
    XLS;



    public static FileType getFileTypeByFileName(String extension) {
        return Arrays.stream(FileType.values())
                .filter(t-> t.toString().equalsIgnoreCase(extension))
                .findAny()
                .orElseThrow();
    }


}
