package com.inspired.azurecomparison.web.controller;

import com.inspired.azurecomparison.domain.FileDifference;
import com.inspired.azurecomparison.service.ComparisonService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@Controller
@RequiredArgsConstructor
public class HomeController {

    private final ComparisonService service;

    @GetMapping("/")
    String index() {
        return "index";
    }



    @PostMapping("/")
    String compareFileData(@RequestParam("file") MultipartFile file, Model model) {
        List<FileDifference> result = service.getResultOfComparisonAccordingFileExtension(file);
        model.addAttribute("message", "Result of comparison");
        model.addAttribute("comparison", result);
        return "index";
    }



}
