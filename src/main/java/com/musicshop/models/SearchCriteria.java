// Add to models/SearchCriteria.java
package com.musicshop.models;

public class SearchCriteria {
    private String keyword;
    private Double minPrice;
    private Double maxPrice;
    private String itemType;
    private Boolean inStock;

    public SearchCriteria(String keyword, Double minPrice, Double maxPrice,
                          String itemType, Boolean inStock) {
        this.keyword = keyword;
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
        this.itemType = itemType;
        this.inStock = inStock;
    }
    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public Double getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(Double minPrice) {
        this.minPrice = minPrice;
    }

    public Double getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(Double maxPrice) {
        this.maxPrice = maxPrice;
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }

    public Boolean getInStock() {
        return inStock;
    }

    public void setInStock(Boolean inStock) {
        this.inStock = inStock;
    }
}
