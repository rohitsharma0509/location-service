package com.scb.location.config;

import lombok.Getter;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ResourceUtils;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;

@Configuration
@Getter
public class GoogleMapsConfig {
    @Value("${secretsPath}")
    private String secretsPath;

    @Value("${api.googleMaps.url}")
    private String url;

    @Value("${api.googleMaps.reverse.geocode}")
    private String reverseGeocodeUrl;

    private String apiKey;


    @SneakyThrows
    @Bean
    public String getApiKey() {

        final URI apiKeyPath = ResourceUtils.getURL(secretsPath + "/GOOGLEMAPS_API_KEY").toURI();
        this.apiKey = sanitize(Files.readAllBytes(Paths.get(apiKeyPath)));
        return apiKey;
    }

    @SneakyThrows
    private String sanitize(byte[] strBytes) {
        return new String(strBytes)
                .replace("\r", "")
                .replace("\n", "");
    }


}
