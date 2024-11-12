// Add to models/SearchCriteria.java
package com.musicshop.models.music;

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
    public String getKeyword() { return keyword; }
    public Double getMinPrice() { return minPrice; }
    public Double getMaxPrice() { return maxPrice; }
    public String getItemType() { return itemType; }
    public Boolean getInStock() { return inStock; }
}
