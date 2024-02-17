package com.inspired.azurecomparison.domain;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FolderForm {
    private MultipartFile readFolder;
    private MultipartFile saveFolder;
}
