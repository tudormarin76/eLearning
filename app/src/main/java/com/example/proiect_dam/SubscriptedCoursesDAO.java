package com.example.proiect_dam;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface SubscriptedCoursesDAO {

    @Insert
    public void insert(SubscriptedCourses course);

    @Insert
    public void insert(List<SubscriptedCourses> courses);

    @Query("Select * from courses where id = :id1")
    public SubscriptedCourses getCourse(long id1);

    @Query("Select * from courses")
    public List<SubscriptedCourses> getAll();

    @Query("Delete from courses")
    public void deleteAll();

    @Query("Delete from courses where id = :id1")
    public void deleteWhere(long id1);

    @Delete
    public void deleteCourse(SubscriptedCourses course);
}
