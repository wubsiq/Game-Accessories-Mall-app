package com.example.dc2.tabledao;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.dc2.DB;
import com.example.dc2.table.SellRecord;

import java.util.ArrayList;
import java.util.List;
public class SellRecordDAO {
    private DB dbHelper;
    private SQLiteDatabase db;

    public SellRecordDAO(Context context) {
        dbHelper = new DB(context);
    }

    public void open() {
        db = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    // 添加出售记录
    public long addSellRecord(SellRecord record) {
        ContentValues values = new ContentValues();
        values.put("user_id", record.getUser_id());
        values.put("item_id", record.getItem_id());
        values.put("sale_price", record.getSale_price());
        values.put("sale_time", record.getSale_time());

        return db.insert("SellRecord", null, values);
    }

    // 根据ID获取出售记录
    @SuppressLint("Range")
    public SellRecord getSellRecordById(int recordId) {
        Cursor cursor = db.query("SellRecord", null, "record_id=?",
                new String[]{String.valueOf(recordId)}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            SellRecord record = new SellRecord();
            record.setRecord_id(cursor.getInt(cursor.getColumnIndex("record_id")));
            record.setUser_id(cursor.getInt(cursor.getColumnIndex("user_id")));
            record.setItem_id(cursor.getInt(cursor.getColumnIndex("item_id")));
            record.setSale_price(cursor.getDouble(cursor.getColumnIndex("sale_price")));
            record.setSale_time(cursor.getString(cursor.getColumnIndex("sale_time")));
            cursor.close();
            return record;
        }
        return null;
    }

    // 更新出售记录
    public int updateSellRecord(SellRecord record) {
        ContentValues values = new ContentValues();
        values.put("user_id", record.getUser_id());
        values.put("item_id", record.getItem_id());
        values.put("sale_price", record.getSale_price());
        values.put("sale_time", record.getSale_time());

        return db.update("SellRecord", values, "record_id=?",
                new String[]{String.valueOf(record.getRecord_id())});
    }

    // 删除出售记录
    public int deleteSellRecord(int recordId) {
        return db.delete("SellRecord", "record_id=?",
                new String[]{String.valueOf(recordId)});
    }

    // 获取所有出售记录
    @SuppressLint("Range")
    public List<SellRecord> getAllSellRecords() {
        List<SellRecord> recordList = new ArrayList<>();
        Cursor cursor = db.query("SellRecord", null, null, null, null, null, "sale_time DESC");

        if (cursor != null) {
            while (cursor.moveToNext()) {
                SellRecord record = new SellRecord();
                record.setRecord_id(cursor.getInt(cursor.getColumnIndex("record_id")));
                record.setUser_id(cursor.getInt(cursor.getColumnIndex("user_id")));
                record.setItem_id(cursor.getInt(cursor.getColumnIndex("item_id")));
                record.setSale_price(cursor.getDouble(cursor.getColumnIndex("sale_price")));
                record.setSale_time(cursor.getString(cursor.getColumnIndex("sale_time")));
                recordList.add(record);
            }
            cursor.close();
        }
        return recordList;
    }

    // 获取用户的出售记录
    @SuppressLint("Range")
    public List<SellRecord> getSellRecordsByUserId(int userId) {
        List<SellRecord> recordList = new ArrayList<>();
        Cursor cursor = db.query("SellRecord", null, "user_id=?",
                new String[]{String.valueOf(userId)}, null, null, "sale_time DESC");

        if (cursor != null) {
            while (cursor.moveToNext()) {
                SellRecord record = new SellRecord();
                record.setRecord_id(cursor.getInt(cursor.getColumnIndex("record_id")));
                record.setUser_id(cursor.getInt(cursor.getColumnIndex("user_id")));
                record.setItem_id(cursor.getInt(cursor.getColumnIndex("item_id")));
                record.setSale_price(cursor.getDouble(cursor.getColumnIndex("sale_price")));
                record.setSale_time(cursor.getString(cursor.getColumnIndex("sale_time")));
                recordList.add(record);
            }
            cursor.close();
        }
        return recordList;
    }
}