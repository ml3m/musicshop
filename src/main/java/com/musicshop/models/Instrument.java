package com.musicshop.models;

public class Instrument extends MusicItem {
    private String type;

    // Default constructor for Jackson
    public Instrument() {
        super(null, 0.0);
        this.type = "instrument";
    }

    public Instrument(String name, double price, String type) {
        super(name, price);
        this.type = type;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return super.toString() + ", Type: " + type;
    }
}
