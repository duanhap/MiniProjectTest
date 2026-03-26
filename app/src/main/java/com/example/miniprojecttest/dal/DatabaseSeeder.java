package com.example.miniprojecttest.dal;

import android.content.Context;

import com.example.miniprojecttest.entities.Category;
import com.example.miniprojecttest.entities.Product;
import com.example.miniprojecttest.entities.User;

/**
 * DatabaseSeeder – Seed dữ liệu mẫu vào DB nếu chưa có dữ liệu.
 *
 * Cách dùng trong Activity (ví dụ MainActivity.onCreate):
 *   DatabaseSeeder.seed(this);
 */
public class DatabaseSeeder {

    public static void seed(Context context) {
        AppDatabase db = AppDatabase.getInstance(context);

        // ── 1. Seed Users ──────────────────────────────────────────────
        if (db.userDAO().getAll().isEmpty()) {
            db.userDAO().insert(new User("admin", "admin123"));
            db.userDAO().insert(new User("nguyen_van_a", "123456"));
            db.userDAO().insert(new User("tran_thi_b", "123456"));
        }

        // ── 2. Seed Categories ─────────────────────────────────────────
        if (db.categoryDAO().getAll().isEmpty()) {
            db.categoryDAO().insert(new Category("Áo"));    // id = 1
            db.categoryDAO().insert(new Category("Quần")); // id = 2
        }

        // ── 3. Seed Products ───────────────────────────────────────────
        if (db.productDAO().getAll().isEmpty()) {

            // ── Áo (categoryId = 1) ──
            db.productDAO().insert(new Product("Áo Thun Trắng",       150_000, "ao0", 1));
            db.productDAO().insert(new Product("Áo Sơ Mi Kẻ Sọc",    220_000, "ao1", 1));
            db.productDAO().insert(new Product("Áo Polo Nam",         280_000, "ao2", 1));
            db.productDAO().insert(new Product("Áo Khoác Dù Nhẹ",    350_000, "ao3", 1));
            db.productDAO().insert(new Product("Áo Hoodie Unisex",    420_000, "ao4", 1));

            // ── Quần (categoryId = 2) ──
            db.productDAO().insert(new Product("Quần Jean Slim Fit",  380_000, "quan0", 2));
            db.productDAO().insert(new Product("Quần Short Thể Thao", 180_000, "quan1", 2));
            db.productDAO().insert(new Product("Quần Kaki Công Sở",   290_000, "quan2", 2));
            db.productDAO().insert(new Product("Quần Jogger Cotton",  250_000, "quan3", 2));
            db.productDAO().insert(new Product("Quần Tây Âu Lịch Sự", 450_000, "quan4", 2));
        }
    }

    /**
     * Lấy Resource ID của ảnh từ tên file (không có extension).
     * Dùng trong Adapter khi muốn load ảnh:
     *   int resId = DatabaseSeeder.getDrawableId(context, product.getImage());
     *   imageView.setImageResource(resId);
     */
    public static int getDrawableId(Context context, String imageName) {
        int resId = context.getResources().getIdentifier(
                imageName, "drawable", context.getPackageName());
        return resId != 0 ? resId : android.R.drawable.ic_menu_gallery;
    }
}
