package com.example.dc2.tabledao;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;
import java.util.List;
import com.example.dc2.DB;
import com.example.dc2.table.GameItem;
import com.example.dc2.table.ItemForSale;

public class GameItemDAO {
    private DB dbHelper;
    private SQLiteDatabase db;

    public GameItemDAO(Context context) {
        dbHelper = new DB(context);
    }

    public void open() {
        db = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    // 添加游戏饰品
    public long addGameItem(GameItem item) {
        ContentValues values = new ContentValues();
        values.put("item_name", item.getItem_name());
        values.put("item_image", item.getItem_image());
        return db.insert("GameItem", null, values);
    }

    // 根据ID获取饰品
    @SuppressLint("Range")
    public GameItem getItemById(int itemId) {
        Cursor cursor = db.query("GameItem", null, "item_id=?",
                new String[]{String.valueOf(itemId)}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            GameItem item = new GameItem();
            item.setItem_id(cursor.getInt(cursor.getColumnIndex("item_id")));
            item.setItem_name(cursor.getString(cursor.getColumnIndex("item_name")));
            item.setItem_image(cursor.getBlob(cursor.getColumnIndex("item_image")));
            cursor.close();
            return item;
        }
        return null;
    }

    // 获取饰品详情（包含价格信息）
    @SuppressLint("Range")
    public GameItem getItemWithPriceInfo(int itemId) {
        GameItem item = getItemById(itemId);
        if (item != null) {
            // 获取价格信息
            String priceQuery = "SELECT MIN(price) AS min_price, COUNT(*) AS on_sale_count " +
                    "FROM ItemForSale WHERE item_id = ?";
            Cursor priceCursor = db.rawQuery(priceQuery, new String[]{String.valueOf(itemId)});

            if (priceCursor != null && priceCursor.moveToFirst()) {
                item.setMin_price(priceCursor.getDouble(priceCursor.getColumnIndex("min_price")));
                item.setOn_sale_count(priceCursor.getInt(priceCursor.getColumnIndex("on_sale_count")));
                priceCursor.close();
            } else {
                item.setMin_price(0);
                item.setOn_sale_count(0);
            }
        }
        return item;
    }


    // 更新饰品信息
    public int updateGameItem(GameItem item) {
        ContentValues values = new ContentValues();
        values.put("item_name", item.getItem_name());
        values.put("item_image", item.getItem_image());
        return db.update("GameItem", values, "item_id=?",
                new String[]{String.valueOf(item.getItem_id())});
    }

    // 删除饰品
    public int deleteGameItem(int itemId) {
        return db.delete("GameItem", "item_id=?",
                new String[]{String.valueOf(itemId)});
    }

    // 获取所有饰品（基础信息）
    @SuppressLint("Range")
    public List<GameItem> getAllGameItems() {
        List<GameItem> itemList = new ArrayList<>();
        Cursor cursor = db.query("GameItem", null, null, null, null, null, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                GameItem item = new GameItem();
                item.setItem_id(cursor.getInt(cursor.getColumnIndex("item_id")));
                item.setItem_name(cursor.getString(cursor.getColumnIndex("item_name")));
                item.setItem_image(cursor.getBlob(cursor.getColumnIndex("item_image")));
                itemList.add(item);
            }
            cursor.close();
        }
        return itemList;
    }

    // 获取所有饰品及其价格信息
    @SuppressLint("Range")
    public List<GameItem> getAllGameItemsWithPriceInfo() {
        List<GameItem> itemList = new ArrayList<>();

        // 使用SQL JOIN一次性获取所有饰品及其价格信息
        String query = "SELECT " +
                "gi.item_id, " +
                "gi.item_name, " +
                "gi.item_image, " +
                "MIN(ifs.price) AS min_price, " +
                "COUNT(ifs.sale_id) AS on_sale_count " +
                "FROM GameItem gi " +
                "LEFT JOIN ItemForSale ifs ON gi.item_id = ifs.item_id " +
                "GROUP BY gi.item_id, gi.item_name, gi.item_image " +
                "ORDER BY on_sale_count DESC";

        Cursor cursor = db.rawQuery(query, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                GameItem item = new GameItem();
                item.setItem_id(cursor.getInt(cursor.getColumnIndex("item_id")));
                item.setItem_name(cursor.getString(cursor.getColumnIndex("item_name")));
                item.setItem_image(cursor.getBlob(cursor.getColumnIndex("item_image")));

                // 处理可能为null的价格信息
                if (!cursor.isNull(cursor.getColumnIndex("min_price"))) {
                    item.setMin_price(cursor.getDouble(cursor.getColumnIndex("min_price")));
                } else {
                    item.setMin_price(0);
                }

                item.setOn_sale_count(cursor.getInt(cursor.getColumnIndex("on_sale_count")));
                itemList.add(item);
            }
            cursor.close();
        }
        return itemList;
    }

    // 搜索游戏饰品（带价格信息）
    @SuppressLint("Range")
    public List<GameItem> searchGameItemsWithPriceInfo(String keyword) {
        List<GameItem> searchResults = new ArrayList<>();

        String query = "SELECT " +
                "gi.item_id, " +
                "gi.item_name, " +
                "gi.item_image, " +
                "MIN(ifs.price) AS min_price, " +
                "COUNT(ifs.sale_id) AS on_sale_count " +
                "FROM GameItem gi " +
                "LEFT JOIN ItemForSale ifs ON gi.item_id = ifs.item_id " +
                "WHERE gi.item_name LIKE ? " +
                "GROUP BY gi.item_id, gi.item_name, gi.item_image " +
                "ORDER BY on_sale_count DESC";

        Cursor cursor = db.rawQuery(query, new String[]{"%" + keyword + "%"});

        if (cursor != null) {
            while (cursor.moveToNext()) {
                GameItem item = new GameItem();
                item.setItem_id(cursor.getInt(cursor.getColumnIndex("item_id")));
                item.setItem_name(cursor.getString(cursor.getColumnIndex("item_name")));
                item.setItem_image(cursor.getBlob(cursor.getColumnIndex("item_image")));

                // 处理可能为null的价格信息
                if (!cursor.isNull(cursor.getColumnIndex("min_price"))) {
                    item.setMin_price(cursor.getDouble(cursor.getColumnIndex("min_price")));
                } else {
                    item.setMin_price(0);
                }

                item.setOn_sale_count(cursor.getInt(cursor.getColumnIndex("on_sale_count")));
                searchResults.add(item);
            }
            cursor.close();
        }
        return searchResults;
    }

    // 获取特定饰品的在售数量
    @SuppressLint("Range")
    public int getOnSaleCountForItem(int itemId) {
        String query = "SELECT COUNT(*) AS count FROM ItemForSale WHERE item_id = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(itemId)});

        int count = 0;
        if (cursor != null && cursor.moveToFirst()) {
            count = cursor.getInt(cursor.getColumnIndex("count"));
            cursor.close();
        }
        return count;
    }

    // 获取特定饰品的最低价格
    @SuppressLint("Range")
    public double getMinPriceForItem(int itemId) {
        String query = "SELECT MIN(price) AS min_price FROM ItemForSale WHERE item_id = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(itemId)});

        double minPrice = 0;
        if (cursor != null && cursor.moveToFirst()) {
            if (!cursor.isNull(cursor.getColumnIndex("min_price"))) {
                minPrice = cursor.getDouble(cursor.getColumnIndex("min_price"));
            }
            cursor.close();
        }
        return minPrice;
    }

    // 分页获取饰品数据（带价格信息）
    @SuppressLint("Range")
    public List<GameItem> getGameItemsWithPriceInfoPaginated(int limit, int offset) {
        List<GameItem> itemList = new ArrayList<>();

        String query = "SELECT " +
                "gi.item_id, " +
                "gi.item_name, " +
                "gi.item_image, " +
                "MIN(ifs.price) AS min_price, " +
                "COUNT(ifs.sale_id) AS on_sale_count " +
                "FROM GameItem gi " +
                "LEFT JOIN ItemForSale ifs ON gi.item_id = ifs.item_id " +
                "GROUP BY gi.item_id, gi.item_name, gi.item_image " +
                "ORDER BY on_sale_count DESC " +
                "LIMIT ? OFFSET ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(limit), String.valueOf(offset)});

        if (cursor != null) {
            while (cursor.moveToNext()) {
                GameItem item = new GameItem();
                item.setItem_id(cursor.getInt(cursor.getColumnIndex("item_id")));
                item.setItem_name(cursor.getString(cursor.getColumnIndex("item_name")));
                item.setItem_image(cursor.getBlob(cursor.getColumnIndex("item_image")));

                // 处理可能为null的价格信息
                if (!cursor.isNull(cursor.getColumnIndex("min_price"))) {
                    item.setMin_price(cursor.getDouble(cursor.getColumnIndex("min_price")));
                } else {
                    item.setMin_price(0);
                }

                item.setOn_sale_count(cursor.getInt(cursor.getColumnIndex("on_sale_count")));
                itemList.add(item);
            }
            cursor.close();
        }
        return itemList;
    }

    // 获取饰品总数（用于分页）
    @SuppressLint("Range")
    public int getTotalGameItemsCount() {
        String query = "SELECT COUNT(*) AS total FROM GameItem";
        Cursor cursor = db.rawQuery(query, null);

        int total = 0;
        if (cursor != null && cursor.moveToFirst()) {
            total = cursor.getInt(cursor.getColumnIndex("total"));
            cursor.close();
        }
        return total;
    }

    // 获取热门饰品（按在售数量排序）
    @SuppressLint("Range")
    public List<GameItem> getPopularGameItems(int limit) {
        List<GameItem> itemList = new ArrayList<>();

        String query = "SELECT " +
                "gi.item_id, " +
                "gi.item_name, " +
                "gi.item_image, " +
                "MIN(ifs.price) AS min_price, " +
                "COUNT(ifs.sale_id) AS on_sale_count " +
                "FROM GameItem gi " +
                "LEFT JOIN ItemForSale ifs ON gi.item_id = ifs.item_id " +
                "GROUP BY gi.item_id, gi.item_name, gi.item_image " +
                "ORDER BY on_sale_count DESC " +
                "LIMIT ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(limit)});

        if (cursor != null) {
            while (cursor.moveToNext()) {
                GameItem item = new GameItem();
                item.setItem_id(cursor.getInt(cursor.getColumnIndex("item_id")));
                item.setItem_name(cursor.getString(cursor.getColumnIndex("item_name")));
                item.setItem_image(cursor.getBlob(cursor.getColumnIndex("item_image")));

                // 处理可能为null的价格信息
                if (!cursor.isNull(cursor.getColumnIndex("min_price"))) {
                    item.setMin_price(cursor.getDouble(cursor.getColumnIndex("min_price")));
                } else {
                    item.setMin_price(0);
                }

                item.setOn_sale_count(cursor.getInt(cursor.getColumnIndex("on_sale_count")));
                itemList.add(item);
            }
            cursor.close();
        }
        return itemList;


    }
    // 在 GameItemDAO 中添加批量查询方法
    @SuppressLint("Range")
    public List<GameItem> getItemsByIds(List<Integer> itemIds) {
        List<GameItem> itemList = new ArrayList<>();
        if (itemIds == null || itemIds.isEmpty()) return itemList;

        // 创建占位符
        StringBuilder placeholders = new StringBuilder();
        for (int i = 0; i < itemIds.size(); i++) {
            placeholders.append("?");
            if (i < itemIds.size() - 1) placeholders.append(",");
        }

        String query = "SELECT * FROM GameItem WHERE item_id IN (" + placeholders + ")";
        String[] args = new String[itemIds.size()];
        for (int i = 0; i < itemIds.size(); i++) {
            args[i] = String.valueOf(itemIds.get(i));
        }

        Cursor cursor = db.rawQuery(query, args);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                GameItem item = new GameItem();
                item.setItem_id(cursor.getInt(cursor.getColumnIndex("item_id")));
                item.setItem_name(cursor.getString(cursor.getColumnIndex("item_name")));
                item.setItem_image(cursor.getBlob(cursor.getColumnIndex("item_image")));
                itemList.add(item);
            }
            cursor.close();
        }
        return itemList;
    }
}