package com.example.payment;

import com.example.Item;

import java.math.BigDecimal;
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

    public void removeItem(Item item) {
        cart.stream().findAny().ifPresent(i ->
        {
            i.equals(item);
            cart.remove(item);
        });
    }

    public BigDecimal getTotal() {
        cart.forEach(item -> {
        });
        return null;
    }

    public List<Item> getCart() {
        return cart;
    }
}
