package com.musicshop.models.music;

public class Album extends MusicItem {
    private String artist;
    private final String type;
    private int year;

    public Album() {
        super(null, 0.0);
        this.type = "album";
    }

    public Album(String name, double price, String artist, int year, String type) {
        this(name, price, artist, year, type, 1, null);
    }

    public Album(String name, double price, String artist, int year, String type, int quantity, String barcode) {
        super(name, price, quantity);
        this.artist = artist;
        this.year = year;
        this.type = type;

        setBarcode(barcode);
    }

    @Override
    public String getType() {
        return type;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getYear() {
        return year;
    }

    @Override
    public String toString() {
        return super.toString() + ", Artist: " + artist + ", Year: " + year;
    }
}
