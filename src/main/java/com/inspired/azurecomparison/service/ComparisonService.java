package com.inspired.azurecomparison.service;

import com.inspired.azurecomparison.domain.FileDifference;
import com.inspired.azurecomparison.enums.FileType;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ComparisonService {

    List<FileDifference> getResultOfComparisonAccordingFileExtension(MultipartFile file);

}
