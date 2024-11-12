package com.musicshop.models.music;

public class Instrument extends MusicItem {
    private final String type;

    // default constructor is required for json
    public Instrument() {
        super(null, 0.0);
        this.type = "instrument";
    }

    public Instrument(String name, double price, String type) {
        this(name, price, type, 1, null);
    }

    public Instrument(String name, double price, String type, int quantity, String barcode) {
        super(name, price, quantity);
        this.type = type;
        setBarcode(barcode);
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
