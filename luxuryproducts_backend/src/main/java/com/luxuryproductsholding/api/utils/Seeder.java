package com.luxuryproductsholding.api.utils;

import com.luxuryproductsholding.api.dao.*;
import com.luxuryproductsholding.api.dto.JsonDTOs.JsonData;
import com.luxuryproductsholding.api.dto.JsonDTOs.JsonProduct;
import com.luxuryproductsholding.api.dto.JsonDTOs.JsonVariant;
import com.luxuryproductsholding.api.models.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class Seeder {

    private final ProductRepository productRepository;
    private final ProductCategoryRepository productCategoryRepository;
    private final VariationRepository variationRepository;
    private final VariationValueRepository variationValueRepository;
    private final ProductVariationRepository productVariationRepository;
    private final UserRepository userRepository;

    @Autowired
    private Environment env;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public Seeder(ProductRepository productRepository,
                  ProductCategoryRepository productCategoryRepository,
                  VariationRepository variationRepository,
                  VariationValueRepository variationValueRepository,
                  ProductVariationRepository productVariationRepository, UserRepository userRepository) {
        this.productRepository = productRepository;
        this.productCategoryRepository = productCategoryRepository;
        this.variationRepository = variationRepository;
        this.variationValueRepository = variationValueRepository;
        this.productVariationRepository = productVariationRepository;
        this.userRepository = userRepository;
    }

    @EventListener
    public void Seed(ContextRefreshedEvent event) {
        this.seedProductsFromJson();
//        this.seedProducts();
        this.seedCustomUser();
    }

    public void seedCustomUser() {
        CustomUser user1 = new CustomUser(
                env.getProperty("USER1_FIRST_NAME"),
                "",
                env.getProperty("USER1_LAST_NAME"),
                "straat", 123, "1234AB", "1990-01-01", "0612345678",
                env.getProperty("USER1_EMAIL"),
                passwordEncoder.encode(env.getProperty("USER1_PASSWORD"))
        );
        user1.setRole(env.getProperty("USER1_ROLE"));
        userRepository.save(user1);

        CustomUser user2 = new CustomUser(
                env.getProperty("USER2_FIRST_NAME"),
                "",
                env.getProperty("USER2_LAST_NAME"),
                "straat", 2, "1233AB", "1990-01-01", "0612444678",
                env.getProperty("USER2_EMAIL"),
                passwordEncoder.encode(env.getProperty("USER2_PASSWORD"))
        );
        user2.setRole(env.getProperty("USER2_ROLE"));
        userRepository.save(user2);

        CustomUser user3 = new CustomUser(
                env.getProperty("USER3_FIRST_NAME"),
                "",
                env.getProperty("USER3_LAST_NAME"),
                "straat", 13, "1214AB", "1990-01-01", "0612395678",
                env.getProperty("USER3_EMAIL"),
                passwordEncoder.encode(env.getProperty("USER3_PASSWORD"))
        );
        user3.setRole(env.getProperty("USER3_ROLE"));
        userRepository.save(user3);
    }

    public void seedProductsFromJson() {
        try {
            JsonData data = loadJsonData();
            Map<String, ProductCategory> categories = ensureCategoriesExist();
            Map<String, Variation> variationMap = new HashMap<>();
            Map<String, VariationValue> variationValueMap = new HashMap<>();

            for (JsonProduct jsonProduct : data.getProducts()) {
                if (productRepository.findByName(jsonProduct.getName()) != null) {
                    System.out.println("Product " + jsonProduct.getName() + " bestaat al, overslaan.");
                    continue;
                }

                ProductCategory category = determineCategory(jsonProduct.getName(), categories);
                Product product = createProduct(jsonProduct, category);
                List<ProductVariation> productVariations = createProductVariations(jsonProduct, product, variationMap, variationValueMap);
                productVariationRepository.saveAll(productVariations);
            }

            System.out.println("Producten en categorieën succesvol toegevoegd.");
        } catch (Exception e) {
            System.err.println("Fout bij inladen JSON: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private JsonData loadJsonData() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        InputStream inputStream = new ClassPathResource("data/all_products_variants_extended.json").getInputStream();
        return mapper.readValue(inputStream, JsonData.class);
    }

    private Map<String, ProductCategory> ensureCategoriesExist() {
        String[] categoryNames = {
                "Accessories", "Wine & Champagne", "Travel & Bags", "Watches",
                "Grooming & Skincare", "Jewelry", "Footwear", "Collectibles", "Sports & Outdoors", "Giftcards"
        };

        Map<String, ProductCategory> categories = new HashMap<>();
        for (String name : categoryNames) {
            ProductCategory category = productCategoryRepository.findByName(name);
            if (category == null) {
                category = new ProductCategory(name);
                productCategoryRepository.save(category);
            }
            categories.put(name, category);
        }
        return categories;
    }

    private ProductCategory determineCategory(String productName, Map<String, ProductCategory> categories) {
        String name = productName.toLowerCase();
        if (name.contains("champagne") || name.contains("pomerol") || name.contains("bordeaux")) {
            return categories.get("Wine & Champagne");
        } else if (name.contains("bag") || name.contains("umbrella") || name.contains("weekender")) {
            return categories.get("Travel & Bags");
        } else if (name.contains("watch") || name.contains("chronograph")) {
            return categories.get("Watches");
        } else if (name.contains("cream") || name.contains("parfum")) {
            return categories.get("Grooming & Skincare");
        } else if (name.contains("bracelet") || name.contains("cufflinks") || name.contains("ring") || name.contains("necklace")) {
            return categories.get("Jewelry");
        } else if (name.contains("loafers")) {
            return categories.get("Footwear");
        } else if (name.contains("model car")) {
            return categories.get("Collectibles");
        } else if (name.contains("skis")) {
            return categories.get("Sports & Outdoors");
        } else if (name.contains("cadeaubon")) {
            return categories.get("Giftcards");
        }

        return categories.get("Accessories");
    }

    private Product createProduct(JsonProduct jsonProduct, ProductCategory category) {
        double basePrice = jsonProduct.getVariants().isEmpty() ? 0 : jsonProduct.getVariants().get(0).getPrice();
        Product product = new Product(
                jsonProduct.getName(),
                basePrice,
                jsonProduct.getDescription(),
                "Unknown Brand",
                category
        );
        return productRepository.save(product);
    }

    private List<ProductVariation> createProductVariations(JsonProduct jsonProduct, Product product,
                                                           Map<String, Variation> variationMap, Map<String, VariationValue> variationValueMap) {

        List<ProductVariation> variations = new ArrayList<>();

        for (JsonVariant variant : jsonProduct.getVariants()) {
            List<VariationValue> values = new ArrayList<>();
            for (Map.Entry<String, String> entry : variant.getDynamicAttributes().entrySet()) {
                String varName = capitalize(entry.getKey());
                String varValue = entry.getValue();

                Variation variation = variationMap.computeIfAbsent(varName, key -> {
                    Variation newVar = new Variation(key);
                    return variationRepository.save(newVar);
                });

                String valueKey = varName + ":" + varValue;
                VariationValue vv = variationValueMap.computeIfAbsent(valueKey, k -> {
                    VariationValue newVal = new VariationValue(varValue, variation);
                    return variationValueRepository.save(newVal);
                });

                values.add(vv);
            }

            String uniqueSku = generateUniqueSku(variant.getSku(), product.getId(), values);

            ProductVariation pv = new ProductVariation(
                    uniqueSku,
                    variant.getPrice(),
                    variant.getStock(),
                    jsonProduct.getImage_urls().isEmpty() ? "" : jsonProduct.getImage_urls().get(0),
                    product,
                    values
            );
            variations.add(pv);
        }

        return variations;
    }

    private String generateUniqueSku(String baseSku, Long productId, List<VariationValue> values) {
        return baseSku + "-" + productId + "-" +
                values.stream()
                        .map(VariationValue::getValue)
                        .collect(Collectors.joining("-"))
                        .toUpperCase()
                        .replaceAll("[^A-Z0-9]", "");
    }

    private String capitalize(String key) {
        return key.substring(0, 1).toUpperCase() + key.substring(1);
    }


}
