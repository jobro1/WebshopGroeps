package com.luxuryproductsholding.api.dto.JsonDTOs;

import java.util.List;
@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)
public class JsonProduct {
    private String name;
    private String description;
    private List<String> image_urls;
    private List<JsonVariant> variants;

    public String getName() { return name; }
    public String getDescription() { return description; }
    public List<String> getImage_urls() { return image_urls; }
    public List<JsonVariant> getVariants() { return variants; }
}
