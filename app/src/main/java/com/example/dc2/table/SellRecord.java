package com.example.dc2.table;

public class SellRecord {
    private int record_id;
    private int user_id;
    private int item_id;
    private double sale_price;
    private String sale_time;

    // 新增：用户对象引用
    private User user;
    // 新增：商品对象引用
    private GameItem gameItem;

    public SellRecord() {
        super();
    }

    public SellRecord(int record_id, int user_id, int item_id, double sale_price, String sale_time) {
        super();
        this.record_id = record_id;
        this.user_id = user_id;
        this.item_id = item_id;
        this.sale_price = sale_price;
        this.sale_time = sale_time;
    }

    public SellRecord(int userId, int itemId, double price, String currentTime) {
    }

    // Getters and Setters
    public int getRecord_id() {
        return record_id;
    }

    public void setRecord_id(int record_id) {
        this.record_id = record_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getItem_id() {
        return item_id;
    }

    public void setItem_id(int item_id) {
        this.item_id = item_id;
    }

    public double getSale_price() {
        return sale_price;
    }

    public void setSale_price(double sale_price) {
        this.sale_price = sale_price;
    }

    public String getSale_time() {
        return sale_time;
    }

    public void setSale_time(String sale_time) {
        this.sale_time = sale_time;
    }

    // 新增：用户对象的getter和setter
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    // 新增：商品对象的getter和setter
    public GameItem getGameItem() {
        return gameItem;
    }

    public void setGameItem(GameItem gameItem) {
        this.gameItem = gameItem;
    }
}