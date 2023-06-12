package Shared.Dto.Artist;

import java.util.ArrayList;

public class CreateArtistDto {
    private String name;
    private int genreId;
    private String biography;
    private int profilePictureId;
    private ArrayList<String> socialMediaLinks;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getGenreId() {
        return genreId;
    }

    public void setGenreId(int genreId) {
        this.genreId = genreId;
    }

    public String getBiography() {
        return biography;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }

    public int getProfilePictureId() {
        return profilePictureId;
    }

    public void setProfilePictureId(int profilePictureId) {
        this.profilePictureId = profilePictureId;
    }

    public ArrayList<String> getSocialMediaLinks() {
        return socialMediaLinks;
    }

    public void setSocialMediaLinks(ArrayList<String> socialMediaLinks) {
        this.socialMediaLinks = socialMediaLinks;
    }
}