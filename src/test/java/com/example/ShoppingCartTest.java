package com.example;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;


class ShoppingCartTest {
    public static final Item TOMATO = new Item(5085, "Tomato", BigDecimal.valueOf(5L), 0, 10);
    public static final Item APPLE = new Item(1500, "Apple", BigDecimal.valueOf(15L), 0, 1);

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

    @Test
    @DisplayName("Test for total cost")
    void returnTotalCostOfShoppingCart() {
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.addItem(TOMATO);
        shoppingCart.addItem(APPLE);
        assertThat(shoppingCart.getItemTotal()).isEqualTo(BigDecimal.valueOf(65));
    }

    @Test
    @DisplayName("Test for total cost with discounted item")
    void returnTotalCostOfShoppingCartWithDiscount() {
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.addItem(TOMATO);
        shoppingCart.addItem(APPLE);
        shoppingCart.addItem(new Item(668, "Cucumber", BigDecimal.valueOf(10L), 50, 2));
        assertThat(shoppingCart.getItemTotal()).isEqualTo(BigDecimal.valueOf(75.0));
    }

    @Test
    @DisplayName("Adding more tomatoes")
    void addingMoreTomatoesShouldIncreaseItemQuantity() {
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.addItem(TOMATO);
        shoppingCart.addItem(new Item(5085, "Tomato", BigDecimal.valueOf(5L), 0, 10));
        assertThat(shoppingCart.getCart().getFirst().quantity()).isEqualTo(20);
    }


}
