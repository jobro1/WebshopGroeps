package com.luxuryproductsholding.api.dto.JsonDTOs;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonVariant {
    private String sku;
    private double price;
    private int stock;
    private String currency;

    private Map<String, Object> allFields;

    @com.fasterxml.jackson.annotation.JsonAnySetter
    void set(String key, Object value) {
        if (allFields == null) allFields = new HashMap<>();
        allFields.put(key, value);
    }

    public String getSku() { return sku; }
    public double getPrice() { return price; }
    public int getStock() { return stock; }

    public Map<String, String> getDynamicAttributes() {
        Map<String, String> attrs = new HashMap<>();
        for (Map.Entry<String, Object> entry : allFields.entrySet()) {
            String key = entry.getKey();
            if (!List.of("sku", "price", "stock", "currency").contains(key)) {
                attrs.put(key, entry.getValue().toString());
            }
        }
        return attrs;
    }
}
