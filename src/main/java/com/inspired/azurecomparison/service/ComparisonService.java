package com.inspired.azurecomparison.service;

import com.inspired.azurecomparison.domain.FileDifference;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ComparisonService {

    List<FileDifference> getResultOfComparisonAccordingFileExtension(MultipartFile file);


}
