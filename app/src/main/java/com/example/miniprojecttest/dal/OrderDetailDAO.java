package com.example.miniprojecttest.dal;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.miniprojecttest.entities.OrderDetail;

import java.util.List;

@Dao
public interface OrderDetailDAO {

    @Insert
    long insert(OrderDetail orderDetail);

    @Query("SELECT * FROM order_details")
    List<OrderDetail> getAll();

    @Query("SELECT * FROM order_details WHERE id = :id LIMIT 1")
    OrderDetail findById(int id);

    @Query("SELECT * FROM order_details WHERE orderId = :orderId")
    List<OrderDetail> findByOrderId(int orderId);

    @Query("SELECT * FROM order_details WHERE orderId = :orderId AND productId = :productId LIMIT 1")
    OrderDetail findByOrderIdAndProductId(int orderId, int productId);

    @Query("UPDATE order_details SET quantity = :quantity, unitPrice = :unitPrice WHERE id = :detailId")
    void updateQuantityAndPrice(int detailId, int quantity, double unitPrice);
}
