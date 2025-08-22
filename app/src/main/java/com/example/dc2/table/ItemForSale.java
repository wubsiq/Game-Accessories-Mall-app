package com.example.dc2.table;

public class ItemForSale {
    private int sale_id;
    private int item_id;
    private int user_id;
    private double price;
    private User user;
    private GameItem gameItem;
    public ItemForSale() {
        super();
    }

    public ItemForSale(int sale_id, int item_id, int user_id, double price) {
        super();
        this.sale_id = sale_id;
        this.item_id = item_id;
        this.user_id = user_id;
        this.price = price;
    }

    // Getters and Setters
    public int getSale_id() {
        return sale_id;
    }

    public void setSale_id(int sale_id) {
        this.sale_id = sale_id;
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

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
    // 辅助方法
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public GameItem getGameItem() {
        return gameItem;
    }

    public void setGameItem(GameItem gameItem) {
        this.gameItem = gameItem;
    }
}