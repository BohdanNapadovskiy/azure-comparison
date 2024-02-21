package com.inspired.azurecomparison.config;

import com.azure.storage.file.share.ShareClient;
import com.azure.storage.file.share.ShareClientBuilder;
import com.azure.storage.file.share.ShareDirectoryClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AzureConfig {


    @Value("${azure.connection-string}")
    private static String connection_string;

    @Value("${azure.account-name}")
    private static String account_name;

    @Value("${azure.share-url}")
    private static String share_url;

    private static String CONNECTION_STRING;
    private static String ACCOUNT_NAME;
    private static String SHARE_URL;


    @Value("${azure.connection-string}")
    public void setConnection_string(String connection_string){
        AzureConfig.CONNECTION_STRING = connection_string;
    }

    @Value("${azure.account-name}")
    public void setAccount_name(String account_name){
        AzureConfig.ACCOUNT_NAME = account_name;
    }

    @Value("${azure.share-url}")
    public void setShare_url(String share_url){
        AzureConfig.SHARE_URL = share_url;
    }

    public static String getConnectionString() {
        return CONNECTION_STRING;
    }

    public static String getAccountName() {
        return ACCOUNT_NAME;
    }

    public static String getShareUrl() {
        return SHARE_URL;
    }
}
