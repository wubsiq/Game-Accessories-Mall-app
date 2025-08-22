package com.example.dc2.tabledao;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.dc2.DB;
import com.example.dc2.table.ItemWantBuy;

import java.util.ArrayList;
import java.util.List;

public class ItemWantBuyDAO {
    private DB dbHelper;
    private SQLiteDatabase db;

    public ItemWantBuyDAO(Context context) {
        dbHelper = new DB(context);
    }

    public void open() {
        db = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    // 添加求购记录
    public long addItemWantBuy(ItemWantBuy item) {
        ContentValues values = new ContentValues();
        values.put("item_id", item.getItem_id());
        values.put("user_id", item.getUser_id());
        values.put("want_price", item.getWant_price());
        values.put("quantity", item.getQuantity());

        return db.insert("ItemWantBuy", null, values);
    }

    // 根据ID获取求购记录
    @SuppressLint("Range")
    public ItemWantBuy getItemWantBuyById(int wantId) {
        Cursor cursor = db.query("ItemWantBuy", null, "want_id=?",
                new String[]{String.valueOf(wantId)}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            ItemWantBuy item = new ItemWantBuy();
            item.setWant_id(cursor.getInt(cursor.getColumnIndex("want_id")));
            item.setItem_id(cursor.getInt(cursor.getColumnIndex("item_id")));
            item.setUser_id(cursor.getInt(cursor.getColumnIndex("user_id")));
            item.setWant_price(cursor.getDouble(cursor.getColumnIndex("want_price")));
            item.setQuantity(cursor.getInt(cursor.getColumnIndex("quantity")));
            cursor.close();
            return item;
        }
        if (cursor != null) {
            cursor.close();
        }
        return null;
    }

    // 更新求购记录
    public int updateItemWantBuy(ItemWantBuy item) {
        ContentValues values = new ContentValues();
        values.put("item_id", item.getItem_id());
        values.put("user_id", item.getUser_id());
        values.put("want_price", item.getWant_price());
        values.put("quantity", item.getQuantity());

        return db.update("ItemWantBuy", values, "want_id=?",
                new String[]{String.valueOf(item.getWant_id())});
    }

    // 删除求购记录
    public int deleteItemWantBuy(int wantId) {
        return db.delete("ItemWantBuy", "want_id=?",
                new String[]{String.valueOf(wantId)});
    }

    // 获取所有求购记录
    @SuppressLint("Range")
    public List<ItemWantBuy> getAllItemsWantBuy() {
        List<ItemWantBuy> wantList = new ArrayList<>();
        Cursor cursor = db.query("ItemWantBuy", null, null, null, null, null, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                ItemWantBuy item = new ItemWantBuy();
                item.setWant_id(cursor.getInt(cursor.getColumnIndex("want_id")));
                item.setItem_id(cursor.getInt(cursor.getColumnIndex("item_id")));
                item.setUser_id(cursor.getInt(cursor.getColumnIndex("user_id")));
                item.setWant_price(cursor.getDouble(cursor.getColumnIndex("want_price")));
                item.setQuantity(cursor.getInt(cursor.getColumnIndex("quantity")));
                wantList.add(item);
            }
            cursor.close();
        }
        return wantList;
    }

    // 获取特定饰品的求购列表
    @SuppressLint("Range")
    public List<ItemWantBuy> getItemsWantBuyByItemId(int itemId) {
        List<ItemWantBuy> wantList = new ArrayList<>();
        Cursor cursor = db.query("ItemWantBuy", null, "item_id=?",
                new String[]{String.valueOf(itemId)}, null, null, "want_price DESC");

        if (cursor != null) {
            while (cursor.moveToNext()) {
                ItemWantBuy item = new ItemWantBuy();
                item.setWant_id(cursor.getInt(cursor.getColumnIndex("want_id")));
                item.setItem_id(cursor.getInt(cursor.getColumnIndex("item_id")));
                item.setUser_id(cursor.getInt(cursor.getColumnIndex("user_id")));
                item.setWant_price(cursor.getDouble(cursor.getColumnIndex("want_price")));
                item.setQuantity(cursor.getInt(cursor.getColumnIndex("quantity")));
                wantList.add(item);
            }
            cursor.close();
        }
        return wantList;
    }

    // 新增：通过用户ID获取求购记录
    @SuppressLint("Range")
    public List<ItemWantBuy> getItemsWantBuyByUserId(int userId) {
        List<ItemWantBuy> wantList = new ArrayList<>();

        // 查询条件：user_id = 指定用户ID
        String selection = "user_id=?";
        String[] selectionArgs = {String.valueOf(userId)};

        // 执行查询
        Cursor cursor = db.query(
                "ItemWantBuy",   // 表名
                null,             // 所有列
                selection,         // WHERE子句
                selectionArgs,     // WHERE参数
                null,             // GROUP BY
                null,             // HAVING
                "want_id DESC"    // 按求购ID降序排列
        );

        // 处理查询结果
        if (cursor != null) {
            while (cursor.moveToNext()) {
                ItemWantBuy item = new ItemWantBuy();
                item.setWant_id(cursor.getInt(cursor.getColumnIndex("want_id")));
                item.setItem_id(cursor.getInt(cursor.getColumnIndex("item_id")));
                item.setUser_id(cursor.getInt(cursor.getColumnIndex("user_id")));
                item.setWant_price(cursor.getDouble(cursor.getColumnIndex("want_price")));
                item.setQuantity(cursor.getInt(cursor.getColumnIndex("quantity")));
                wantList.add(item);
            }
            cursor.close();
        }
        return wantList;
    }
}