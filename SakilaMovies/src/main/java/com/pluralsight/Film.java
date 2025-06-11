package com.pluralsight;

public class Film {
    private int filmId;
    private String title;
    private String description;
    private int releaseYear;
    private int length;

    public Film(int filmId, String title, String description, int releaseYear, int length) {
        this.filmId = filmId;
        this.title = title;
        this.description = description;
        this.releaseYear = releaseYear;
        this.length = length;
    }

    public int getFilmId() { return filmId; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public int getReleaseYear() { return releaseYear; }
    public int getLength() { return length; }

    @Override
    public String toString() {
        return "filmId: " + filmId + "\n"
                + "title: " + title + "\n"
                + "description: " + description + "\n"
                + "releaseYear: " + releaseYear + "\n"
                + "length: " + length + "\n"
                + "------------------------";
    }
}
