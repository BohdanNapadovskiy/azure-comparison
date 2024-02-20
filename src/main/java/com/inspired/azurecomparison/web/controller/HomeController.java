package com.inspired.azurecomparison.web.controller;

import com.inspired.azurecomparison.domain.FileDifference;
import com.inspired.azurecomparison.service.AzureFileOperation;
import com.inspired.azurecomparison.service.ComparisonService;
import com.inspired.azurecomparison.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Controller
@RequiredArgsConstructor
public class HomeController {

    private final AzureFileOperation azureFileOperation;

    @GetMapping("/")
    String index() {
        return "index";
    }

    @PostMapping("/compare")
    public String compareFileAndCreateReport(@RequestParam("azureFolder") String azureFolder,
                                             @RequestParam("reportFolder") String reportFolder,
                                             Model model) {
        Map<String, List<FileDifference>> result = azureFileOperation.compareAzureFilesWithDataBase(azureFolder, reportFolder);
        model.addAttribute("comparison", result);
        model.addAttribute("azureFolder", azureFolder);
        model.addAttribute("reportFolder", reportFolder);
        model.addAttribute("submitting", false);
        return "index";
    }


}
