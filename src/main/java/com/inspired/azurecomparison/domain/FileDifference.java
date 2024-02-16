package com.inspired.azurecomparison.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FileDifference {
    private int lineNumber;
    private String fileData;
    private String databaseData;


    private FileDifference() {
    }

    public static FileDifference buildFileDifference(int lineNumber, String fileData, String databaseData) {
        FileDifference fileDifference = new FileDifference();
        fileDifference.lineNumber = lineNumber;
        fileDifference.fileData = fileData;
        fileDifference.databaseData = databaseData;
        return fileDifference;
    }
}
