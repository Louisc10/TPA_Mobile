package com.example.readup.model;

public class NovelModel {
    private String novelAuthor;
    private Integer novelGenre;
    private String novelName;
    private String novelContent;

    public NovelModel(String novelName, Integer novelGenre, String novelAuthor, String novelContent) {
        this.novelAuthor = novelAuthor;
        this.novelGenre = novelGenre;
        this.novelName = novelName;
        this.novelContent = novelContent;
    }

    public String getNovelContent() {
        return novelContent;
    }

    public void setNovelContent(String novelContent) {
        this.novelContent = novelContent;
    }

    public String getNovelAuthor() {
        return novelAuthor;
    }

    public void setNovelAuthor(String novelAuthor) {
        this.novelAuthor = novelAuthor;
    }

    public Integer getNovelGenre() {
        return novelGenre;
    }

    public void setNovelGenre(Integer novelGenre) {
        this.novelGenre = novelGenre;
    }

    public String getNovelName() {
        return novelName;
    }

    public void setNovelName(String novelName) {
        this.novelName = novelName;
    }
}
