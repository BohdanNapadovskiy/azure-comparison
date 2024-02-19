package com.inspired.azurecomparison.web.controller;

import com.inspired.azurecomparison.domain.FileDifference;
import com.inspired.azurecomparison.service.AzureFileOperation;
import com.inspired.azurecomparison.service.ComparisonService;
import com.inspired.azurecomparison.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;


@Controller
@RequiredArgsConstructor
public class HomeController {

    private final ReportService reportService;
    private final ComparisonService comparisonService;
    private final AzureFileOperation azureFileOperation;

    @GetMapping("/")
    String index() {
        return "index";
    }

    @PostMapping("/compare")
    public String compareFileAndCreateReport(@RequestParam("compareFiles") MultipartFile[] compareFiles,
                                             Model model) {
        String errorMessage = null;
        List<FileDifference> result = new ArrayList<>();
        if (compareFiles.length == 1) {
            for (MultipartFile file : compareFiles) {
                result = comparisonService.getResultOfComparisonAccordingFileExtension(file);
            }
            model.addAttribute("message", "Result of comparison");
            model.addAttribute("comparison", result);
            reportService.createCSVReport(result);
        } else if (compareFiles.length > 1) {
            errorMessage = "Folder do not contain files";
            model.addAttribute("error-message", errorMessage);
        } else {
            errorMessage = "Folders contain more than ine file";
            model.addAttribute("error-message", errorMessage);
        }
        return "index";
    }

    @GetMapping("/azure")
    public void compareFileFromAzure() {
        List<FileDifference> result = azureFileOperation.compareAzureFileWithDataBase("filesharedemo", "employees.csv");
//        model.addAttribute("message", "Result of comparison");
//        model.addAttribute("comparison", result);
        reportService.createCSVReport(result);

    }


}
