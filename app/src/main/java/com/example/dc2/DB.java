package com.example.dc2;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DB extends SQLiteOpenHelper {
    // 版本号更新为5（因为表结构有变更）
    private static final int VERSION = 5;
    private static final String DBNAME = "GameItemTrade2.db";
    private Context mContext;

    public DB(Context context) {
        super(context, DBNAME, null, VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 创建用户表（包含头像和密保字段）
        db.execSQL("CREATE TABLE User (" +
                "user_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "username TEXT UNIQUE NOT NULL," +
                "password TEXT NOT NULL," +
                "wallet_balance REAL DEFAULT 0.0," +
                "is_admin INTEGER DEFAULT 0," +  // 0-普通用户 1-管理员
                "avatar BLOB," +  // 存储头像二进制数据
                "phone TEXT," +
                "email TEXT," +
                "security_question TEXT," + // 新增密保问题
                "security_answer TEXT" +    // 新增密保答案
                ");");

        // 创建游戏饰品表（包含图片的BLOB字段）
        db.execSQL("CREATE TABLE GameItem (" +
                "item_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "item_name TEXT NOT NULL," +
                "item_image BLOB" +  // 存储饰品图片二进制数据
                ");");

        // 创建饰品出售表
        db.execSQL("CREATE TABLE ItemForSale (" +
                "sale_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "item_id INTEGER NOT NULL," +
                "user_id INTEGER NOT NULL," +
                "price REAL NOT NULL," +
                "FOREIGN KEY(item_id) REFERENCES GameItem(item_id)," +
                "FOREIGN KEY(user_id) REFERENCES User(user_id)" +
                ");");

        // 创建饰品求购表
        db.execSQL("CREATE TABLE ItemWantBuy (" +
                "want_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "item_id INTEGER NOT NULL," +
                "user_id INTEGER NOT NULL," +
                "want_price REAL NOT NULL," +
                "quantity INTEGER DEFAULT 1," +
                "FOREIGN KEY(item_id) REFERENCES GameItem(item_id)," +
                "FOREIGN KEY(user_id) REFERENCES User(user_id)" +
                ");");

        // 创建购买记录表
        db.execSQL("CREATE TABLE BuyRecord (" +
                "record_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "user_id INTEGER NOT NULL," +  // 购买者ID
                "item_id INTEGER NOT NULL," +
                "purchase_price REAL NOT NULL," +
                "purchase_time DATETIME DEFAULT CURRENT_TIMESTAMP," +  // 交易时间
                "FOREIGN KEY(user_id) REFERENCES User(user_id)," +
                "FOREIGN KEY(item_id) REFERENCES GameItem(item_id)" +
                ");");

        // 创建出售记录表
        db.execSQL("CREATE TABLE SellRecord (" +
                "record_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "user_id INTEGER NOT NULL," +  // 出售者ID
                "item_id INTEGER NOT NULL," +
                "sale_price REAL NOT NULL," +
                "sale_time DATETIME DEFAULT CURRENT_TIMESTAMP," +  // 交易时间
                "FOREIGN KEY(user_id) REFERENCES User(user_id)," +
                "FOREIGN KEY(item_id) REFERENCES GameItem(item_id)" +
                ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 处理数据库升级
        if (oldVersion < 5) {
            // 从旧版本升级到版本5（添加密保字段）
            db.execSQL("ALTER TABLE User ADD COLUMN security_question TEXT");
            db.execSQL("ALTER TABLE User ADD COLUMN security_answer TEXT");
        }

        // 如果旧版本小于4，则删除重建（因为之前版本没有密保字段）
        if (oldVersion < 4) {
            db.execSQL("DROP TABLE IF EXISTS User");
            db.execSQL("DROP TABLE IF EXISTS GameItem");
            db.execSQL("DROP TABLE IF EXISTS ItemForSale");
            db.execSQL("DROP TABLE IF EXISTS ItemWantBuy");
            db.execSQL("DROP TABLE IF EXISTS BuyRecord");
            db.execSQL("DROP TABLE IF EXISTS SellRecord");
            onCreate(db);
        }
    }

    // 启用外键支持
    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            db.execSQL("PRAGMA foreign_keys = ON;"); // 启用外键约束
        }
    }
}