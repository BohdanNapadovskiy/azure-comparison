package com.inspired.azurecomparison.service.impl;

import com.azure.storage.file.share.ShareClient;
import com.azure.storage.file.share.ShareDirectoryClient;
import com.inspired.azurecomparison.domain.AzureForm;
import com.inspired.azurecomparison.domain.FileDifference;
import com.inspired.azurecomparison.service.FileComparingService;
import com.inspired.azurecomparison.service.ReportService;
import com.inspired.azurecomparison.service.db.DataDynamicService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;


import java.util.List;

@Service
@RequiredArgsConstructor
public class FileComparingServiceImpl implements FileComparingService {

    private final ReportService reportService;
    private final CompareDispatcher dispatcher;
    private final AzureFileOperation fileOperation;
    private final DataDynamicService dataDynamicService;

    @Override
    public void compareAzureFilesWithDataBase(AzureForm azureForm) {
        ShareClient shareClient = fileOperation.buildGetShareClient(azureForm.getAzureStorageAccount(), azureForm.getAzureFileShareName());
        ShareDirectoryClient rootDirectoryClient = shareClient.getDirectoryClient("");
        ShareDirectoryClient directoryClient = fileOperation.createDirectoryClient(rootDirectoryClient, "", azureForm.getAzureFolder());
        listFilesAndFolders(directoryClient, azureForm.getAzureFolder(), azureForm.getReportFolder());
    }

    private void listFilesAndFolders(ShareDirectoryClient directoryClient, String parentPath, String reportDirectory) {
        directoryClient.listFilesAndDirectories().stream()
                .forEach(shareFileItem -> {
                    if (shareFileItem.isDirectory()) {
                        listFilesAndFolders(directoryClient.getSubdirectoryClient(shareFileItem.getName()), parentPath + "/" + shareFileItem.getName(), reportDirectory);
                    } else {
                        createResultOfDifference(directoryClient, shareFileItem.getName(), reportDirectory);
                    }
                });
    }


    public void createResultOfDifference(ShareDirectoryClient directoryClient, String fileName, String reportDirectory) {
        String filePath = fileOperation.downloadFileFromShareClient(directoryClient, fileName);
        List<FileDifference> result = comparingFileDataWithDataBaseData(filePath);
        reportService.createCSVReport(result, reportDirectory, fileName);
        fileOperation.deleteDownloadedFile(filePath);
    }


    private List<FileDifference> comparingFileDataWithDataBaseData(String file) {
        String fileName = FilenameUtils.getBaseName(file);
        List<String> databaseResult = dataDynamicService.getAllData(fileName);
        return dispatcher.compareDataFromFileAndDataBase(file, databaseResult);
    }


}
