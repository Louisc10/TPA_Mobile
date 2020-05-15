package com.example.readup.model;

import androidx.annotation.NonNull;

public class Genre {
    private String genreName;

    public Genre(String genreName) {
        this.genreName = genreName;
    }

    public String getGenreName() {
        return genreName;
    }

    public void setGenreName(String genreName) {
        this.genreName = genreName;
    }

    @NonNull
    @Override
    public String toString() {
        return genreName;
    }
}
