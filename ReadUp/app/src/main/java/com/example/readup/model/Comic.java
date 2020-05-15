package com.example.readup.model;

public class Comic {
    public String comicName;
    public String comicGenre;
    public String comicAuthor;

    public Comic(String comicName, String comicGenre, String comicAuthor) {
        this.comicName = comicName;
        this.comicGenre = comicGenre;
        this.comicAuthor = comicAuthor;
    }

    public String getComicName() {
        return comicName;
    }

    public void setComicName(String comicName) {
        this.comicName = comicName;
    }

    public String getComicGenre() {
        return comicGenre;
    }

    public void setComicGenre(String comicGenre) {
        this.comicGenre = comicGenre;
    }

    public String getComicAuthor() {
        return comicAuthor;
    }

    public void setComicAuthor(String comicAuthor) {
        this.comicAuthor = comicAuthor;
    }
}
