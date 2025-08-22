package com.example.dc2.tabledao;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.dc2.DB;
import com.example.dc2.table.BuyRecord;
import com.example.dc2.table.GameItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BuyRecordDAO {
    private DB dbHelper;
    public SQLiteDatabase db;
    private GameItemDAO gameItemDAO;
    private Context context;

    public BuyRecordDAO(Context context) {
        this.context = context;
        dbHelper = new DB(context);
        gameItemDAO = new GameItemDAO(context);
    }

    public void open() {
        db = dbHelper.getWritableDatabase();
        gameItemDAO.open();
    }

    public void close() {
        if (db != null && db.isOpen()) {
            db.close();
        }
        gameItemDAO.close();
        dbHelper.close();
    }

    // 添加购买记录
    public long addBuyRecord(BuyRecord record) {
        return addBuyRecord(db, record);
    }

    public long addBuyRecord(SQLiteDatabase db, BuyRecord record) {
        ContentValues values = new ContentValues();
        values.put("user_id", record.getUser_id());
        values.put("item_id", record.getItem_id());
        values.put("purchase_price", record.getPurchase_price());
        values.put("purchase_time", record.getPurchase_time());
        return db.insert("BuyRecord", null, values);
    }

    // 根据ID获取购买记录
    @SuppressLint("Range")
    public BuyRecord getBuyRecordById(int recordId) {
        return getBuyRecordById(recordId, false);
    }

    // 重载方法：可选择是否关联商品信息
    @SuppressLint("Range")
    public BuyRecord getBuyRecordById(int recordId, boolean withGameItem) {
        Cursor cursor = db.query("BuyRecord", null, "record_id=?",
                new String[]{String.valueOf(recordId)}, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            BuyRecord record = new BuyRecord();
            record.setRecord_id(cursor.getInt(cursor.getColumnIndex("record_id")));
            record.setUser_id(cursor.getInt(cursor.getColumnIndex("user_id")));
            record.setItem_id(cursor.getInt(cursor.getColumnIndex("item_id")));
            record.setPurchase_price(cursor.getDouble(cursor.getColumnIndex("purchase_price")));
            record.setPurchase_time(cursor.getString(cursor.getColumnIndex("purchase_time")));
            cursor.close();

            // 如果需要关联商品信息
            if (withGameItem) {
                record.setGameItem(getGameItemByItemId(record.getItem_id()));
            }
            return record;
        }
        return null;
    }

    // 通过购买记录ID获取游戏饰品
    public GameItem getGameItemByRecordId(int recordId) {
        BuyRecord record = getBuyRecordById(recordId);
        if (record != null) {
            return getGameItemByItemId(record.getItem_id());
        }
        return null;
    }

    // 通过item_id获取游戏饰品
    public GameItem getGameItemByItemId(int itemId) {
        return gameItemDAO.getItemById(itemId);
    }

    // 通过item_id获取带价格信息的游戏饰品
    public GameItem getGameItemWithPriceInfoByItemId(int itemId) {
        return gameItemDAO.getItemWithPriceInfo(itemId);
    }

    // 更新购买记录
    public int updateBuyRecord(BuyRecord record) {
        ContentValues values = new ContentValues();
        values.put("user_id", record.getUser_id());
        values.put("item_id", record.getItem_id());
        values.put("purchase_price", record.getPurchase_price());
        values.put("purchase_time", record.getPurchase_time());
        return db.update("BuyRecord", values, "record_id=?",
                new String[]{String.valueOf(record.getRecord_id())});
    }

    // 删除购买记录
    public int deleteBuyRecord(int recordId) {
        return db.delete("BuyRecord", "record_id=?",
                new String[]{String.valueOf(recordId)});
    }

    // 获取所有购买记录
    @SuppressLint("Range")
    public List<BuyRecord> getAllBuyRecords(boolean withGameItem) {
        List<BuyRecord> recordList = new ArrayList<>();
        Cursor cursor = db.query("BuyRecord", null, null, null, null, null, "purchase_time DESC");

        if (cursor != null) {
            while (cursor.moveToNext()) {
                BuyRecord record = new BuyRecord();
                record.setRecord_id(cursor.getInt(cursor.getColumnIndex("record_id")));
                record.setUser_id(cursor.getInt(cursor.getColumnIndex("user_id")));
                record.setItem_id(cursor.getInt(cursor.getColumnIndex("item_id")));
                record.setPurchase_price(cursor.getDouble(cursor.getColumnIndex("purchase_price")));
                record.setPurchase_time(cursor.getString(cursor.getColumnIndex("purchase_time")));

                // 关联商品信息
                if (withGameItem) {
                    record.setGameItem(getGameItemByItemId(record.getItem_id()));
                }

                recordList.add(record);
            }
            cursor.close();
        }
        return recordList;
    }

    // 获取用户的购买记录
    @SuppressLint("Range")
    public List<BuyRecord> getBuyRecordsByUserId(int userId, boolean withGameItem) {
        List<BuyRecord> recordList = new ArrayList<>();
        Cursor cursor = db.query("BuyRecord", null, "user_id=?",
                new String[]{String.valueOf(userId)}, null, null, "purchase_time DESC");

        if (cursor != null) {
            while (cursor.moveToNext()) {
                BuyRecord record = new BuyRecord();
                record.setRecord_id(cursor.getInt(cursor.getColumnIndex("record_id")));
                record.setUser_id(cursor.getInt(cursor.getColumnIndex("user_id")));
                record.setItem_id(cursor.getInt(cursor.getColumnIndex("item_id")));
                record.setPurchase_price(cursor.getDouble(cursor.getColumnIndex("purchase_price")));
                record.setPurchase_time(cursor.getString(cursor.getColumnIndex("purchase_time")));

                // 关联商品信息
                if (withGameItem) {
                    record.setGameItem(getGameItemByItemId(record.getItem_id()));
                }

                recordList.add(record);
            }
            cursor.close();
        }
        return recordList;
    }

    // 获取用户的购买记录并关联商品信息（批量优化）
    public List<BuyRecord> getBuyRecordsWithGameItemsByUserId(int userId) {
        List<BuyRecord> records = getBuyRecordsByUserId(userId, false);

        // 收集所有需要查询的商品ID
        List<Integer> itemIds = new ArrayList<>();
        for (BuyRecord record : records) {
            itemIds.add(record.getItem_id());
        }

        // 批量获取商品信息
        Map<Integer, GameItem> itemMap = new HashMap<>();
        if (!itemIds.isEmpty()) {
            // 假设 GameItemDAO 已实现批量查询方法
            gameItemDAO.open();
            List<GameItem> items = gameItemDAO.getItemsByIds(itemIds);
            gameItemDAO.close();

            for (GameItem item : items) {
                itemMap.put(item.getItem_id(), item);
            }
        }

        // 关联商品信息
        for (BuyRecord record : records) {
            GameItem item = itemMap.get(record.getItem_id());
            if (item != null) {
                record.setGameItem(item);
            }
        }

        return records;
    }

    // 根据购买记录设置游戏饰品信息
    public void setGameItemForRecord(BuyRecord record) {
        if (record != null && record.getItem_id() > 0) {
            record.setGameItem(getGameItemByItemId(record.getItem_id()));
        }
    }

    // 根据购买记录设置带价格信息的游戏饰品
    public void setGameItemWithPriceInfoForRecord(BuyRecord record) {
        if (record != null && record.getItem_id() > 0) {
            record.setGameItem(getGameItemWithPriceInfoByItemId(record.getItem_id()));
        }
    }
}