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
        if (item != null)
            cart.add(item);
    }

    public void deleteItem(Item item) {
        cart.stream().findAny().ifPresent(i ->
        {i.equals(item);
        cart.remove(item);});
    }

    public List<Item> getCart() {
        return cart;
    }
}
