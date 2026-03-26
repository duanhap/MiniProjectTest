package com.example.miniprojecttest.dal;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.miniprojecttest.entities.Product;

import java.util.List;

@Dao
public interface ProductDAO {

    @Insert
    long insert(Product product);

    @Query("SELECT * FROM products")
    List<Product> getAll();

    @Query("SELECT * FROM products WHERE id = :id LIMIT 1")
    Product findById(int id);

    @Query("SELECT * FROM products WHERE categoryId = :categoryId")
    List<Product> findByCategoryId(int categoryId);

    @Query("SELECT * FROM products WHERE name LIKE '%' || :keyword || '%' ORDER BY name ASC")
    List<Product> searchByName(String keyword);

    @Query("SELECT * FROM products WHERE categoryId = :categoryId AND name LIKE '%' || :keyword || '%' ORDER BY name ASC")
    List<Product> searchByCategoryAndName(int categoryId, String keyword);
}
