package com.musicshop.models;

public class Album extends MusicItem {
    private String artist;
    private String type;
    private int year;

    // Default constructor for Jackson
    public Album() {
        super(null, 0.0);
        this.type = "album";
    }

    public Album(String name, double price, String artist, int year, String type) {
        super(name, price);
        this.artist = artist;
        this.year = year;
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public String getArtist() {
        return artist;
    }

    public int getYear() {
        return year;
    }

    @Override
    public String toString() {
        return super.toString() + ", Artist: " + artist + ", Year: " + year;
    }
}
