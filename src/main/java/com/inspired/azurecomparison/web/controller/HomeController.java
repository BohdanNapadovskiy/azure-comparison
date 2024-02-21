package com.inspired.azurecomparison.web.controller;

import com.inspired.azurecomparison.service.FileComparingService;
import com.inspired.azurecomparison.service.impl.AzureFileOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
@RequiredArgsConstructor
public class HomeController {

    private final FileComparingService comparingService;

    @GetMapping("/")
    String index() {
        return "index";
    }

    @PostMapping("/compare")
    public String compareFileAndCreateReport(@RequestParam("azureFolder") String azureFolder,
                                             @RequestParam("reportFolder") String reportFolder,
                                             Model model) {
        comparingService.compareAzureFilesWithDataBase(azureFolder, reportFolder);
        model.addAttribute("azureFolder", azureFolder);
        model.addAttribute("reportFolder", reportFolder);
        model.addAttribute("submitting", false);
        return "index";
    }


}
