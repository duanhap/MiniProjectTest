package com.example.miniprojecttest.dal;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.miniprojecttest.entities.Category;

import java.util.List;

@Dao
public interface CategoryDAO {

    @Insert
    long insert(Category category);

    @Query("SELECT * FROM categories")
    List<Category> getAll();

    @Query("SELECT * FROM categories WHERE id = :id LIMIT 1")
    Category findById(int id);
}
