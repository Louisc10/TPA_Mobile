package com.example.readup.model;

public class Novel {
    public String novelName;
    public String novelGenre;
    public String novelAuthor;

    public Novel(String novelName, String novelGenre, String novelAuthor) {
        this.novelName = novelName;
        this.novelGenre = novelGenre;
        this.novelAuthor = novelAuthor;
    }

    public String getNovelName() {
        return novelName;
    }

    public void setNovelName(String novelName) {
        this.novelName = novelName;
    }

    public String getNovelGenre() {
        return novelGenre;
    }

    public void setNovelGenre(String novelGenre) {
        this.novelGenre = novelGenre;
    }

    public String getNovelAuthor() {
        return novelAuthor;
    }

    public void setNovelAuthor(String novelAuthor) {
        this.novelAuthor = novelAuthor;
    }
}
