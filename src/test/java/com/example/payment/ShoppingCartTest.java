package com.example.payment;

import com.example.Item;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;


class ShoppingCartTest {
    public static final Item TOMATO = new Item(5085,"Tomato", BigDecimal.valueOf(5L), 0, 10);

    @Test
    @DisplayName("Test for adding item")
    void AddItemToShoppingCart() {
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.addItem(TOMATO);
        assertThat(shoppingCart.getCart()).contains(TOMATO);
    }

    @Test
    @DisplayName("Test for adding null item")
    void addingNullItemDoesNotIncreaseCartSize() {
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.addItem(null);
        assertThat(shoppingCart.getCart()).isEmpty();
    }

    @Test
    @DisplayName("Test for removing item")
    void removeItemFromShoppingCart() {
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.addItem(TOMATO);
        shoppingCart.removeItem(TOMATO);
        assertThat(shoppingCart.getCart()).doesNotContain(TOMATO);
    }


}
