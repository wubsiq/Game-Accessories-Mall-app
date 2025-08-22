package com.example.dc2.table;

public class ItemWantBuy {
    private int want_id;
    private int item_id;
    private int user_id;
    private double want_price;
    private int quantity;

    public ItemWantBuy() {
        super();
    }

    public ItemWantBuy(int want_id, int item_id, int user_id, double want_price, int quantity) {
        super();
        this.want_id = want_id;
        this.item_id = item_id;
        this.user_id = user_id;
        this.want_price = want_price;
        this.quantity = quantity;
    }

    // Getters and Setters
    public int getWant_id() {
        return want_id;
    }

    public void setWant_id(int want_id) {
        this.want_id = want_id;
    }

    public int getItem_id() {
        return item_id;
    }

    public void setItem_id(int item_id) {
        this.item_id = item_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public double getWant_price() {
        return want_price;
    }

    public void setWant_price(double want_price) {
        this.want_price = want_price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}