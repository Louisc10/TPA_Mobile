package com.example.readup.model;

public class ComicModel {

    private String comicAuthor;
    private Integer comicGenre;
    private String comicName;

    public ComicModel(String comicName,Integer comicGenre,String comicAuthor){
        this.comicAuthor=comicAuthor;
        this.comicGenre=comicGenre;
        this.comicName=comicName;
    }

    public String getcomicAuthor() {
        return comicAuthor;
    }

    public void setcomicAuthor(String comicAuthor) {
        this.comicAuthor = comicAuthor;
    }

    public Integer getComicGenre() {
        return comicGenre;
    }

    public void setComicGenre(Integer comicGenre) {
        this.comicGenre = comicGenre;
    }

    public String getComicName() {
        return comicName;
    }

    public void setComicName(String comicName) {
        this.comicName = comicName;
    }
}
