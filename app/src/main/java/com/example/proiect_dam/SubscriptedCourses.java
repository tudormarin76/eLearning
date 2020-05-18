package com.example.proiect_dam;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "courses")
public class SubscriptedCourses {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String title;

    @Ignore
    public SubscriptedCourses() {
    }

    public SubscriptedCourses(int id, String title) {
        this.id = id;
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return title;
    }
}
