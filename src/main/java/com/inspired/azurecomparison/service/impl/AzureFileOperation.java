package com.inspired.azurecomparison.service.impl;

import com.azure.storage.common.StorageInputStream;
import com.azure.storage.file.share.*;
import com.azure.storage.file.share.models.ShareFileItem;
import com.azure.storage.file.share.models.ShareStorageException;
import com.inspired.azurecomparison.config.AzureConfig;
import com.inspired.azurecomparison.domain.FileDifference;
import com.inspired.azurecomparison.service.FileComparingService;
import com.inspired.azurecomparison.service.ReportService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;

@Service

public class AzureFileOperation {



    public ShareClient buildGetShareClient(String accountName,String fileStorage) {
        return new ShareClientBuilder().endpoint(String.format(AzureConfig.getShareUrl(), accountName))
                .connectionString(AzureConfig.getConnectionString()).shareName(fileStorage).buildClient();
    }

    public ShareDirectoryClient createDirectoryClient(ShareDirectoryClient directoryClient, String rootDirectory, String azureDirectory) {
        for (ShareFileItem fileClient : directoryClient.listFilesAndDirectories()) {
            if (fileClient.isDirectory()) {

                if (fileClient.getName().equalsIgnoreCase(azureDirectory)) {
                    return directoryClient.getSubdirectoryClient(fileClient.getName());
                } else {
                    return createDirectoryClient(directoryClient.getSubdirectoryClient(fileClient.getName()), rootDirectory + "/" + fileClient.getName(), azureDirectory);
                }
            }
        }
        return directoryClient;
    }

    public void deleteDownloadedFile(String filePath) {
        File downloadFile = new File(filePath);
        if (Files.exists(downloadFile.toPath())) {
            downloadFile.delete();
        }
    }

    public String downloadFileFromShareClient(ShareDirectoryClient directoryClient, String fileName) {
        try {
            ShareFileClient fileClient = directoryClient.getFileClient(fileName);
            fileClient.downloadToFile(fileName);
        } catch (ShareStorageException e) {
            System.out.println("Failed to download file from storage. Reasons: " + e.getMessage());
        }
        return fileName;

    }

}
