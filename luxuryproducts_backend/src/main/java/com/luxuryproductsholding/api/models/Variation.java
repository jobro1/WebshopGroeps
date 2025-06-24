package com.luxuryproductsholding.api.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

import java.util.List;

@Entity
public class Variation {
    @Id
    @GeneratedValue
    private long variationId;
    private String variationName;

    @OneToMany(mappedBy = "variation", cascade = CascadeType.MERGE)
    @JsonBackReference
    private List<VariationValue> valueList;

    public Variation() {
    }

    public Variation(String variationName) {
        this.variationName = variationName;
    }

    public long getVariationId() {
        return variationId;
    }

    public String getVariationName() {
        return variationName;
    }

    public List<VariationValue> getValueList() {
        return valueList;
    }

    public void setVariationId(long variationId) {
        this.variationId = variationId;
    }

    public void setVariationName(String variationName) {
        this.variationName = variationName;
    }

    public void setValueList(List<VariationValue> valueList) {
        this.valueList = valueList;
    }

    public void setName(String variationName) {
    }
}
