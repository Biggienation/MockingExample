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
        if (item == null) {
            return;
        }

        for (Item i : cart) {
            if (i.barcode() == item.barcode()) {
                cart.add(new Item(i.barcode(), i.name(), i.price(), item.discount(), i.quantity() + item.quantity()));
                cart.remove(i);
                return;
            }
        }

        cart.add(item);
    }

    public void removeItem(Item item) {
        if (item == null) {
            return;
        }
        cart.removeIf(i -> i.barcode() == item.barcode());
    }

    public BigDecimal getTotal() {
        return cart.stream()
                .map(this :: calculateItemTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calculateItemTotal(Item item) {
        BigDecimal itemTotal = item.price().multiply(BigDecimal.valueOf(item.quantity()));
        if (item.discount() > 0) {
           BigDecimal discountAmount = itemTotal.multiply(BigDecimal.valueOf((double) item.discount()/100));
           itemTotal = itemTotal.subtract(discountAmount);
        }
        return itemTotal;
    }

    private static BigDecimal getBigDecimal(Item item) {
        return item.price().multiply(BigDecimal.valueOf(item.quantity()));
    }

    private static BigDecimal getBigDecimal(Item item, BigDecimal multiply) {
        return multiply.multiply(BigDecimal.valueOf(((double) item.discount() / 100)));
    }

    public List<Item> getCart() {
        return new ArrayList<>(cart);
    }
}
