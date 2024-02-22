package com.inspired.azurecomparison.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.bind.annotation.RequestParam;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AzureForm {
    private String azureFolder;
    private String reportFolder;
    private String azureStorageAccount;
    private String azureFileShareName;
}
