package com.inspired.azurecomparison.service;

import com.azure.core.util.Configuration;
import com.azure.storage.file.share.ShareFileClient;
import com.azure.storage.file.share.ShareFileClientBuilder;
import com.azure.storage.file.share.models.ShareStorageException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@Service
public class AzureFileOperation {

    @Value("${azure.connection-string}")
    private static String CONNECTION_STRING;

    @Value("${azure.account-name}")
    private static String ACCOUNT_NAME;

    public String getFileFromAzureStorage() {

        String fileURL = String.format("https://%s.file.core.windows.net", ACCOUNT_NAME);
        ShareFileClient fileClient = new ShareFileClientBuilder()
            .connectionString(CONNECTION_STRING)
            .endpoint(fileURL)
//            .shareName(shareName)
//            .resourcePath(directoryPath + "/" + fileName)
            .buildFileClient();

//        String downloadPath = filePath + "testfiles/" + "downloadSample.txt";
        String downloadPath = "testfiles/";
        File downloadFile = new File(downloadPath);
        try {
            if (!Files.exists(downloadFile.toPath()) && !downloadFile.createNewFile()) {
                throw new RuntimeException("Failed to create new upload file.");
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to create new upload file.");
        }
        downloadFile.delete();
        try {
            fileClient.downloadToFile(downloadPath);
        } catch (ShareStorageException e) {
            System.out.println("Failed to download file from storage. Reasons: " + e.getMessage());
        }

        if (Files.exists(downloadFile.toPath()) && !downloadFile.delete()) {
            System.out.println("Failed to delete download file.");
        }

        return downloadPath;

    }

}
