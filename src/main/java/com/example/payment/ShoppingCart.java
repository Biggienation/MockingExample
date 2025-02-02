package com.example.payment;

import com.example.Item;

import java.util.ArrayList;
import java.util.List;

public class ShoppingCart {
    private final List<Item> cart;

    public ShoppingCart() {
        cart = new ArrayList<>();
    }

    public void addItem(Item item) {
        cart.add(item);
    }

    public List<Item> getCart() {
        return cart;
    }
}
