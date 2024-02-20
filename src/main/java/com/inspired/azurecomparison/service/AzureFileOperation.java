package com.inspired.azurecomparison.service;

import com.azure.storage.file.share.*;
import com.azure.storage.file.share.models.ShareFileItem;
import com.azure.storage.file.share.models.ShareStorageException;
import com.inspired.azurecomparison.domain.FileDifference;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;

@Service

public class AzureFileOperation {
    private final ReportService reportService;
    private final ComparisonAzureService comparisonService;


    @Value("${azure.connection-string}")
    private String CONNECTION_STRING;

    @Value("${azure.account-name}")
    private String ACCOUNT_NAME;

    @Value("${azure.share-url}")
    private String SHARE_URL;

    @Value("${azure.share-name}")
    private String SHARE_NAME;

    public AzureFileOperation(ReportService reportService, ComparisonAzureService comparisonService) {
        this.reportService = reportService;
        this.comparisonService = comparisonService;
    }

    public HashMap<String, List<FileDifference>> compareAzureFilesWithDataBase(String azureDirectory, String reportDirectory) {
        HashMap<String, List<FileDifference>> result = new HashMap<>();
        String shareURL = String.format(SHARE_URL, ACCOUNT_NAME);
        ShareClient shareClient = new ShareClientBuilder().endpoint(shareURL)
                .connectionString(CONNECTION_STRING).shareName(SHARE_NAME).buildClient();
        ShareDirectoryClient directoryClient = createDirectoryClient(shareClient.getDirectoryClient(""), "",azureDirectory);
        listFilesAndFolders(directoryClient, azureDirectory, result, reportDirectory);
        return result;
    }

    private ShareDirectoryClient createDirectoryClient(ShareDirectoryClient directoryClient, String rootDirectory, String azureDirectory) {
        ShareDirectoryClient shareDirectoryClient = directoryClient;
        for (ShareFileItem fileClient : directoryClient.listFilesAndDirectories()) {
            if (fileClient.isDirectory()) {

                if (fileClient.getName().equalsIgnoreCase(azureDirectory)) {
                    return directoryClient.getSubdirectoryClient(fileClient.getName());
                } else {
                    return createDirectoryClient(directoryClient.getSubdirectoryClient(fileClient.getName()), rootDirectory + "/" + fileClient.getName(), azureDirectory);
                }
            }
        }
        return shareDirectoryClient;
    }


    private void listFilesAndFolders(ShareDirectoryClient directoryClient, String parentPath, HashMap<String, List<FileDifference>> result, String reportDirectory) {
        directoryClient.listFilesAndDirectories().stream()
                .forEach(shareFileItem -> {
                    if (shareFileItem.isDirectory()) {
                        listFilesAndFolders(directoryClient.getSubdirectoryClient(shareFileItem.getName()), parentPath + "/" + shareFileItem.getName(), result, reportDirectory);
                    } else {
                        List<FileDifference> difference = createResultOfDifference(directoryClient, shareFileItem.getName(), reportDirectory);
                        if (!difference.isEmpty()) {
                            result.put(shareFileItem.getName(), difference);
                        }
                    }
                });
    }

    private List<FileDifference> createResultOfDifference(ShareDirectoryClient directoryClient, String fileName, String reportDirectory) {
        String filePath = downloadFileFromShareClient(directoryClient, fileName);
        List<FileDifference> result = comparisonService.getResultOfComparisonAccordingFileExtension(filePath);
        reportService.createCSVReport(result, reportDirectory, fileName);
        deleteDownloadedFile(filePath);
        return result;
    }

    private void deleteDownloadedFile(String filePath) {
        File downloadFile = new File(filePath);
        if (Files.exists(downloadFile.toPath())) {
            downloadFile.delete();
        }
    }

    private String downloadFileFromShareClient(ShareDirectoryClient directoryClient, String fileName) {
        try {
            ShareFileClient fileClient = directoryClient.getFileClient(fileName);
            fileClient.downloadToFile(fileName);
        } catch (ShareStorageException e) {
            System.out.println("Failed to download file from storage. Reasons: " + e.getMessage());
        }
        return fileName;

    }

}
