package com.example.miniprojecttest.dal;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.miniprojecttest.entities.Order;

import java.util.List;

@Dao
public interface OrderDAO {

    @Insert
    long insert(Order order);

    @Query("SELECT * FROM orders")
    List<Order> getAll();

    @Query("SELECT * FROM orders WHERE id = :id LIMIT 1")
    Order findById(int id);

    @Query("SELECT * FROM orders WHERE userId = :userId")
    List<Order> findByUserId(int userId);
}
