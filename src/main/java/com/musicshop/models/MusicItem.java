package com.musicshop.models;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonSubTypes;

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type"  // This is the key for the subtype
)
@JsonSubTypes({
    @JsonSubTypes.Type(value = Album.class, name = "album"),
    @JsonSubTypes.Type(value = Instrument.class, name = "instrument")
})
public abstract class MusicItem {
    protected String name;
    protected double price;

    // Constructor
    public MusicItem(String name, double price) {
        this.name = name;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return "Name: " + name + ", Price: $" + price;
    }
}
