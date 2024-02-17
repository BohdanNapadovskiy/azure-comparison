package com.inspired.azurecomparison.web.controller;

import com.inspired.azurecomparison.domain.FolderForm;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

@Controller
public class FolderSelectionController {


    @GetMapping("/folders")
    public String showFolderSelectionForm(Model model) {
        model.addAttribute("folderForm", new FolderForm());
        return "folderSelection";
    }

    @PostMapping("/folders")
    public String handleFolderSelectionForm(@RequestParam("readFolder") MultipartFile readFolder,
                                            @RequestParam("saveFolder") MultipartFile saveFolder,
                                            Model model) {
        try {
            String readFolderPath = saveUploadedFiles(readFolder, "read");
            String saveFolderPath = saveUploadedFiles(saveFolder, "save");

            model.addAttribute("readFolderPath", readFolderPath);
            model.addAttribute("saveFolderPath", saveFolderPath);

            return "folderResult";
        } catch (IOException e) {
            model.addAttribute("error", "Error processing files: " + e.getMessage());
            return "folderSelection";
        }
    }

    private String saveUploadedFiles(MultipartFile folder, String folderType) throws IOException {
        String folderPath = "uploads" + File.separator + folderType + File.separator;
        Path directory = Path.of(folderPath);

        if (!Files.exists(directory)) {
            Files.createDirectories(directory);
        }

        String folderName = folder.getOriginalFilename();
        String filePath = folderPath + folderName;
        Path destination = Path.of(filePath);
        Files.copy(folder.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);

        return filePath;
    }
}
