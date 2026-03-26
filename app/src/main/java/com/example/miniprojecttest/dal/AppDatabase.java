package com.example.miniprojecttest.dal;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.miniprojecttest.entities.Category;
import com.example.miniprojecttest.entities.Order;
import com.example.miniprojecttest.entities.OrderDetail;
import com.example.miniprojecttest.entities.Product;
import com.example.miniprojecttest.entities.User;

@Database(
        entities = {User.class, Category.class, Product.class, Order.class, OrderDetail.class},
    version = 2,
        exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase INSTANCE;

    // DAOs
    public abstract UserDAO userDAO();
    public abstract CategoryDAO categoryDAO();
    public abstract ProductDAO productDAO();
    public abstract OrderDAO orderDAO();
    public abstract OrderDetailDAO orderDetailDAO();

    /**
     * Singleton – trả về instance duy nhất của database.
     * Gọi từ Activity: AppDatabase db = AppDatabase.getInstance(this);
     */
    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class,
                                    "mini_project_db"
                            )
                            .allowMainThreadQueries()   // Cho phép gọi trực tiếp từ Activity (đơn giản hóa)
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
