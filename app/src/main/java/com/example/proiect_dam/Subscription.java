package com.example.proiect_dam;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(foreignKeys = {@ForeignKey(entity = User.class,
                            parentColumns = "id",
                            childColumns = "userId",
                            onDelete = ForeignKey.CASCADE),
                        @ForeignKey(entity = SubscriptedCourses.class,
                            parentColumns = "id",
                            childColumns = "subscriptedCourseId",
                            onDelete = ForeignKey.CASCADE)},
        tableName = "subscriptions")
public class Subscription {

    @PrimaryKey(autoGenerate = true)
    private int id;
    public int userId;
    public int subscriptedCourseId;
    public String data;

    @Ignore
    public Subscription(){}

    public Subscription(int id, int userId, int subscriptedCourseId, String data) {
        this.id = id;
        this.userId = userId;
        this.subscriptedCourseId = subscriptedCourseId;
        this.data = data;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getSubscriptedCourseId() {
        return subscriptedCourseId;
    }

    public void setSubscriptedCourseId(int subscriptedCourseId) {
        this.subscriptedCourseId = subscriptedCourseId;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "Subscription{" +
                "id=" + id +
                ", userId=" + userId +
                ", subscriptedCourseId=" + subscriptedCourseId +
                ", data='" + data + '\'' +
                '}';
    }
}
