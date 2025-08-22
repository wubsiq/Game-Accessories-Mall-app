package com.example.dc2.table;

public class GameItem {
    private int item_id;
    private String item_name;
    private byte[] item_image;
    private double min_price;      // 新增：饰品最低售价
    private int on_sale_count;    // 新增：在售数量

    public GameItem() {
        super();
        // 初始化新增字段
        this.min_price = 0;
        this.on_sale_count = 0;
    }

    public GameItem(int item_id, String item_name, byte[] item_image) {
        super();
        this.item_id = item_id;
        this.item_name = item_name;
        this.item_image = item_image;
        // 初始化新增字段
        this.min_price = 0;
        this.on_sale_count = 0;
    }

    // Getters and Setters
    public int getItem_id() {
        return item_id;
    }

    public void setItem_id(int item_id) {
        this.item_id = item_id;
    }

    public String getItem_name() {
        return item_name;
    }

    public void setItem_name(String item_name) {
        this.item_name = item_name;
    }

    public byte[] getItem_image() {
        return item_image;
    }

    public void setItem_image(byte[] item_image) {
        this.item_image = item_image;
    }

    // 新增属性的getter和setter
    public double getMin_price() {
        return min_price;
    }

    public void setMin_price(double min_price) {
        this.min_price = min_price;
    }

    public int getOn_sale_count() {
        return on_sale_count;
    }

    public void setOn_sale_count(int on_sale_count) {
        this.on_sale_count = on_sale_count;
    }
}