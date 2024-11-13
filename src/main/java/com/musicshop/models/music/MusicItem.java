package com.musicshop.models.music;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonIgnore;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type"
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = Album.class, name = "album"),
    @JsonSubTypes.Type(value = Instrument.class, name = "instrument")
})

public abstract class MusicItem {
    protected String name;
    protected double price;
    protected int quantity;

    protected String barcode;

    // default constructor is required for json
    public MusicItem() { }

    // Default quantity to 1
    public MusicItem(String name, double price) {
        this(name, price, 1);
    }

    public MusicItem(String name, double price, int quantity) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getBarcode() { return barcode; }
    public void setBarcode(String barcode) { this.barcode = barcode; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public void increaseQuantity(int amount) { this.quantity += amount; }


    @Override
    public String toString() {
        return "Name: " + name + ", Price: $" + price + ", Quantity: " + quantity;
    }

    public abstract String getType();
}
