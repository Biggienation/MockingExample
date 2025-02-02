package com.example;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ShoppingCart {
    private final List<Item> cart;

    public ShoppingCart() {
        cart = new ArrayList<>();
    }

    public void addItem(Item item) {
        if (item == null) {
            return;
        }

        Optional<Item> existingItem = findItemByBarCode(item.barcode());
        if (existingItem.isPresent()) {
            updateItemQuantity(existingItem.get(), item.quantity());
        } else {
            cart.add(item);
        }
    }

    private Optional<Item> findItemByBarCode(int barcode) {
        return cart.stream()
                .filter(item -> item.barcode() == barcode)
                .findFirst();
    }

    private void updateItemQuantity(Item itemFormCart, int additionalQuantity) {
        Item updatedItem = new Item(itemFormCart.barcode(),
                itemFormCart.name(),
                itemFormCart.price(),
                itemFormCart.discount(),
                itemFormCart.quantity() + additionalQuantity);
        cart.remove(itemFormCart);
        cart.add(updatedItem);
    }

    public void removeItem(Item item) {
        if (item == null) {
            return;
        }
        cart.removeIf(i -> i.barcode() == item.barcode());
    }

    public BigDecimal getItemTotal() {
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

    public List<Item> getCart() {
        return new ArrayList<>(cart);
    }
}
