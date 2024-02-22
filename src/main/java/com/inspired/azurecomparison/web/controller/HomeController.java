package com.inspired.azurecomparison.web.controller;

import com.inspired.azurecomparison.service.FileComparingService;
import com.inspired.azurecomparison.domain.AzureForm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;


@Controller
@RequiredArgsConstructor
public class HomeController {

    private final FileComparingService comparingService;

    @GetMapping("/")
    String index() {
        return "index";
    }

    @PostMapping("/compare")
    public String compareFileAndCreateReport(@ModelAttribute("azureForm") AzureForm azureForm, Model model) {
        comparingService.compareAzureFilesWithDataBase(azureForm);
        model.addAttribute("azureFolder", azureForm.getAzureFolder());
        model.addAttribute("reportFolder", azureForm.getReportFolder());
        model.addAttribute("azureStorageAccount", azureForm.getAzureStorageAccount());
        model.addAttribute("azureFileShareName", azureForm.getAzureFileShareName());

        model.addAttribute("submitting", false);
        return "index";
    }


}
