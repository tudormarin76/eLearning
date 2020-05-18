package com.example.proiect_dam;

import androidx.annotation.NonNull;

public class News {

    private String link;
    private String title;
    private String description;

    public News(String link, String title, String description) {
        this.link = link;
        this.title = title;
        this.description = description;
    }

    public String getLink() {
        return link;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "News{" +
                "link='" + link + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
