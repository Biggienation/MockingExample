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
        BigDecimal total = BigDecimal.ZERO;
        for (Item item : cart) {
            if (item.discount() == 0) {
                total = total.add(getBigDecimal(item));
            } else {
                total = total.add(getBigDecimal(item, getBigDecimal(item)));
            }
        }
        return total;
    }

    private static BigDecimal getBigDecimal(Item item) {
        return item.price().multiply(BigDecimal.valueOf(item.quantity()));
    }

    private static BigDecimal getBigDecimal(Item item, BigDecimal multiply) {
         return multiply.multiply(BigDecimal.valueOf(((double) item.discount() / 100)));
    }

    public List<Item> getCart() {
        return cart;
    }
}
