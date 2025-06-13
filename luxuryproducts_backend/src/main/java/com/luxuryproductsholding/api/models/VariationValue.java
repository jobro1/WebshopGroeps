package com.luxuryproductsholding.api.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.util.List;

@Entity
public class VariationValue {
    @Id
    @GeneratedValue
    private long variationValueId;
    private String value;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "variation_id")
    @JsonManagedReference
    private Variation variation;

    @ManyToMany(mappedBy = "values")
    @JsonBackReference
    private List<ProductVariation> productVariations;

    public VariationValue() {
    }

    public VariationValue(String value, Variation variation) {
        this.value = value;
        this.variation = variation;
    }

    public long getVariationValueId() {
        return variationValueId;
    }

    public String getValue() {
        return value;
    }

    public Variation getVariation() {
        return variation;
    }

    public List<ProductVariation> getProductVariations() {
        return productVariations;
    }

    public void setVariationValueId(long variationValueId) {
        this.variationValueId = variationValueId;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setVariation(Variation variation) {
        this.variation = variation;
    }

    public void setProductVariations(List<ProductVariation> productVariations) {
        this.productVariations = productVariations;
    }
}
