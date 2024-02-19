package com.inspired.azurecomparison.service;

import com.inspired.azurecomparison.domain.FileDifference;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ComparisonAzureService {

    List<FileDifference> getResultOfComparisonAccordingFileExtension(String downloadedFile);
}
