package com.example.proiect_dam;

import android.widget.ListView;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface SubscriptionDAO {
    @Insert
    public void insert(Subscription subscription);

    @Insert
    public void insert(List<Subscription> subscription);

    @Query("Select * from subscriptions where userId=:uID")
    public List<Subscription> getAll(int uID);

    @Query("Select * from subscriptions where userId =:uID and subscriptedCourseId = :cID")
    public Subscription getSubscription(int uID, int cID);

    @Query("Select c.id,c.title from courses c, subscriptions s where s.userId =:uID and s.subscriptedCourseId = c.id")
    public List<SubscriptedCourses> getUserCourses(int uID);

    @Query("Delete from subscriptions")
    public void deleteAll();

    @Query("Delete from subscriptions where id = :id1")
    public void deleteWhere(long id1);

    @Delete
    public void deleteSubscription(Subscription subscription);

}
