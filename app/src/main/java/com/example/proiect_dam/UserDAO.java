package com.example.proiect_dam;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface UserDAO {

    @Insert
    public void insert(User user);

    @Insert
    public void insert(List<User> users);

    @Query("Select * from users")
    public List<User> getAll();

    @Query("Select * from users where id = :id1")
    public User getUser(int id1);

    @Query("Delete from users")
    public void deleteAll();

    @Query("Delete from users where id = :id1")
    public void deleteWhere(long id1);

    @Delete
    public void deleteUser(User user);

}
