package com.example.dc2.table;

import java.util.Date;

public class BuyRecord {
    private int record_id;
    private int user_id;
    private int item_id;
    private double purchase_price;
    private String purchase_time;
    private GameItem gameItem; // 商品对象
    private User user;         // 新增：用户对象

    // 构造器
    public BuyRecord() {}

    public BuyRecord(int user_id, int item_id, double purchase_price, String purchase_time) {
        this.user_id = user_id;
        this.item_id = item_id;
        this.purchase_price = purchase_price;
        this.purchase_time = purchase_time;
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

    public double getPurchase_price() {
        return purchase_price;
    }

    public void setPurchase_price(double purchase_price) {
        this.purchase_price = purchase_price;
    }

    public String getPurchase_time() {
        return purchase_time;
    }

    public void setPurchase_time(String purchase_time) {
        this.purchase_time = purchase_time;
    }

    // 商品对象的getter和setter
    public GameItem getGameItem() {
        return gameItem;
    }

    public void setGameItem(GameItem gameItem) {
        this.gameItem = gameItem;
    }

    // 新增：用户对象的getter和setter
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}