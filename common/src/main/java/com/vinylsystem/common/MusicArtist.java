package com.vinylsystem.common;

/**
 * Represents a music artist from Discogs API
 */
public class MusicArtist {
    private String id;
    private String name;
    private String realName;
    private String profile;
    private String[] aliases;
    private String[] images;
    private String[] genres;
    
    // Default constructor for JSON parsing
    public MusicArtist() {}
    
    public MusicArtist(String id, String name) {
        this.id = id;
        this.name = name;
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getRealName() { return realName; }
    public void setRealName(String realName) { this.realName = realName; }
    
    public String getProfile() { return profile; }
    public void setProfile(String profile) { this.profile = profile; }
    
    public String[] getAliases() { return aliases; }
    public void setAliases(String[] aliases) { this.aliases = aliases; }
    
    public String[] getImages() { return images; }
    public void setImages(String[] images) { this.images = images; }
    
    public String[] getGenres() { return genres; }
    public void setGenres(String[] genres) { this.genres = genres; }
    
    @Override
    public String toString() {
        return String.format("MusicArtist{id='%s', name='%s', realName='%s'}",
                id, name, realName);
    }
}