package com.inspired.azurecomparison.service;

import com.azure.core.util.Configuration;
import com.azure.storage.file.share.ShareFileClient;
import com.azure.storage.file.share.ShareFileClientBuilder;
import com.azure.storage.file.share.models.ShareStorageException;
import com.inspired.azurecomparison.domain.FileDifference;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AzureFileOperation {

    private final ReportService reportService;
    ;
    private final ComparisonAzureService comparisonService;


    @Value("${azure.connection-string}")
    private String CONNECTION_STRING;

    @Value("${azure.account-name}")
    private String ACCOUNT_NAME;

    public List<FileDifference> compareAzureFileWithDataBase(String shareName, String resourcePath) {
        String filePath = getAzureFileFromComparing(shareName, resourcePath);
        List<FileDifference> result = comparisonService.getResultOfComparisonAccordingFileExtension(filePath);
        reportService.createCSVReport(result);
        deleteDownloadedFile(filePath);
        return result;
    }

    private void deleteDownloadedFile(String filePath) {
        File downloadFile = new File(filePath);
        if (Files.exists(downloadFile.toPath())) {
            downloadFile.delete();
        }
    }


    private String getAzureFileFromComparing(String shareName, String resourcePath) {
        String fileURL = String.format("https://%s.file.core.windows.net", ACCOUNT_NAME);
        ShareFileClient fileClient = new ShareFileClientBuilder()
                .connectionString(CONNECTION_STRING)
                .endpoint(fileURL)
                .shareName(shareName)
                .resourcePath(resourcePath)
//            .shareName("filesharedemo")
//            .resourcePath("tatauranga-umanga-maori-statistics-on-maori-businesses-2021-update-corrected.csv")
                .buildFileClient();

        String downloadPath = resourcePath;
        try {
            fileClient.downloadToFile(downloadPath);
        } catch (ShareStorageException e) {
            System.out.println("Failed to download file from storage. Reasons: " + e.getMessage());
        }
        return downloadPath;

    }

}
