package com.vinylsystem.common;

/**
 * Represents a music release (album/single) from Discogs API
 */
public class MusicRelease {
    private String id;
    private String title;
    private String artist;
    private String year;
    private String genre;
    private String style;
    private String format;
    private String label;
    private String catalogNumber;
    private String coverImage;
    private String country;
    
    // Default constructor for JSON parsing
    public MusicRelease() {}
    
    public MusicRelease(String id, String title, String artist, String year) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.year = year;
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getArtist() { return artist; }
    public void setArtist(String artist) { this.artist = artist; }
    
    public String getYear() { return year; }
    public void setYear(String year) { this.year = year; }
    
    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }
    
    public String getStyle() { return style; }
    public void setStyle(String style) { this.style = style; }
    
    public String getFormat() { return format; }
    public void setFormat(String format) { this.format = format; }
    
    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }
    
    public String getCatalogNumber() { return catalogNumber; }
    public void setCatalogNumber(String catalogNumber) { this.catalogNumber = catalogNumber; }
    
    public String getCoverImage() { return coverImage; }
    public void setCoverImage(String coverImage) { this.coverImage = coverImage; }
    
    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }
    
    @Override
    public String toString() {
        return String.format("MusicRelease{id='%s', title='%s', artist='%s', year='%s', genre='%s', format='%s'}",
                id, title, artist, year, genre, format);
    }
}