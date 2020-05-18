package com.example.proiect_dam;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {User.class, SubscriptedCourses.class, Subscription.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public final static String DB_NAME = "database.db";
    private static AppDatabase instanta;

    public static AppDatabase getInstance(Context context){
        if(instanta == null){
            instanta = Room.databaseBuilder(context, AppDatabase.class, DB_NAME).allowMainThreadQueries().fallbackToDestructiveMigration().build();
        }
        return instanta;
    }

    public abstract UserDAO getUserDao();
    public abstract SubscriptionDAO getSubscriptionDao();
    public abstract SubscriptedCoursesDAO getCoursesDao();

}
