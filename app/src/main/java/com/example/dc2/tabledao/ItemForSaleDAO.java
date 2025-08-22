package com.example.dc2.tabledao;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.dc2.DB;
import com.example.dc2.table.ItemForSale;

import java.util.ArrayList;
import java.util.List;

public class ItemForSaleDAO {
    private DB dbHelper;
    public SQLiteDatabase db;
    private static final String TAG = "ItemForSaleDAO";
    public ItemForSaleDAO(Context context) {
        dbHelper = new DB(context);
    }

    public void open() {
        db = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }


    @SuppressLint("Range")
    public List<ItemForSale> getItemsForSaleByUserId(int userId) {
        List<ItemForSale> saleList = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Log.d(TAG, "查询用户ID: " + userId + " 的在售物品");

        // 确保表名和列名正确
        String query = "SELECT * FROM ItemForSale WHERE user_id = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId)});

        if (cursor != null) {
            Log.d(TAG, "查询结果数量: " + cursor.getCount());

            while (cursor.moveToNext()) {
                ItemForSale item = new ItemForSale();
                item.setSale_id(cursor.getInt(cursor.getColumnIndex("sale_id")));
                item.setItem_id(cursor.getInt(cursor.getColumnIndex("item_id")));
                item.setUser_id(cursor.getInt(cursor.getColumnIndex("user_id")));
                item.setPrice(cursor.getDouble(cursor.getColumnIndex("price")));

                Log.d(TAG, "找到在售物品: " + item.getSale_id() +
                        ", 商品ID: " + item.getItem_id() +
                        ", 价格: " + item.getPrice());

                saleList.add(item);
            }
            cursor.close();
        } else {
            Log.w(TAG, "查询返回空指针");
        }

        db.close();
        return saleList;
    }

    // 添加在售饰品
    public long addItemForSale(ItemForSale item) {
        ContentValues values = new ContentValues();
        values.put("item_id", item.getItem_id());
        values.put("user_id", item.getUser_id());
        values.put("price", item.getPrice());

        return db.insert("ItemForSale", null, values);
    }

    // 根据ID获取在售饰品
    @SuppressLint("Range")
    public ItemForSale getItemForSaleById(int saleId) {
        Cursor cursor = db.query("ItemForSale", null, "sale_id=?",
                new String[]{String.valueOf(saleId)}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            ItemForSale item = new ItemForSale();
            item.setSale_id(cursor.getInt(cursor.getColumnIndex("sale_id")));
            item.setItem_id(cursor.getInt(cursor.getColumnIndex("item_id")));
            item.setUser_id(cursor.getInt(cursor.getColumnIndex("user_id")));
            item.setPrice(cursor.getDouble(cursor.getColumnIndex("price")));
            cursor.close();
            return item;
        }
        return null;
    }

    // 更新在售饰品信息
    public int updateItemForSale(ItemForSale item) {
        ContentValues values = new ContentValues();
        values.put("item_id", item.getItem_id());
        values.put("user_id", item.getUser_id());
        values.put("price", item.getPrice());

        return db.update("ItemForSale", values, "sale_id=?",
                new String[]{String.valueOf(item.getSale_id())});
    }

    // 删除在售饰品
    public int deleteItemForSale(int saleId) {
        return db.delete("ItemForSale", "sale_id=?",
                new String[]{String.valueOf(saleId)});
    }

    // 获取所有在售饰品
    @SuppressLint("Range")
    public List<ItemForSale> getAllItemsForSale() {
        List<ItemForSale> saleList = new ArrayList<>();
        Cursor cursor = db.query("ItemForSale", null, null, null, null, null, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                ItemForSale item = new ItemForSale();
                item.setSale_id(cursor.getInt(cursor.getColumnIndex("sale_id")));
                item.setItem_id(cursor.getInt(cursor.getColumnIndex("item_id")));
                item.setUser_id(cursor.getInt(cursor.getColumnIndex("user_id")));
                item.setPrice(cursor.getDouble(cursor.getColumnIndex("price")));
                saleList.add(item);
            }
            cursor.close();
        }
        return saleList;
    }

    // 获取特定饰品的在售列表（按价格升序）
    @SuppressLint("Range")
    public List<ItemForSale> getItemsForSaleByItemId(int itemId) {
        List<ItemForSale> saleList = new ArrayList<>();
        Cursor cursor = db.query("ItemForSale", null, "item_id=?",
                new String[]{String.valueOf(itemId)}, null, null, "price ASC");

        if (cursor != null) {
            while (cursor.moveToNext()) {
                ItemForSale item = new ItemForSale();
                item.setSale_id(cursor.getInt(cursor.getColumnIndex("sale_id")));
                item.setItem_id(cursor.getInt(cursor.getColumnIndex("item_id")));
                item.setUser_id(cursor.getInt(cursor.getColumnIndex("user_id")));
                item.setPrice(cursor.getDouble(cursor.getColumnIndex("price")));
                saleList.add(item);
            }
            cursor.close();
        }
        return saleList;
    }

    // 获取特定饰品的在售列表，支持额外筛选条件
    @SuppressLint("Range")
    public List<ItemForSale> getItemsForSaleByItemId(int itemId, String selection, String[] selectionArgs) {
        List<ItemForSale> saleList = new ArrayList<>();

        // 构建完整的查询条件
        String fullSelection = "item_id=?";
        if (selection != null && !selection.isEmpty()) {
            fullSelection += " AND " + selection;
        }

        // 合并查询参数
        String[] fullSelectionArgs;
        if (selectionArgs != null && selectionArgs.length > 0) {
            fullSelectionArgs = new String[1 + selectionArgs.length];
            fullSelectionArgs[0] = String.valueOf(itemId);
            System.arraycopy(selectionArgs, 0, fullSelectionArgs, 1, selectionArgs.length);
        } else {
            fullSelectionArgs = new String[]{String.valueOf(itemId)};
        }

        // 执行查询并处理结果
        Cursor cursor = db.query("ItemForSale", null, fullSelection,
                fullSelectionArgs, null, null, "price ASC");

        if (cursor != null) {
            while (cursor.moveToNext()) {
                ItemForSale item = new ItemForSale();
                item.setSale_id(cursor.getInt(cursor.getColumnIndex("sale_id")));
                item.setItem_id(cursor.getInt(cursor.getColumnIndex("item_id")));
                item.setUser_id(cursor.getInt(cursor.getColumnIndex("user_id")));
                item.setPrice(cursor.getDouble(cursor.getColumnIndex("price")));
                saleList.add(item);
            }
            cursor.close();
        }
        return saleList;
    }
}