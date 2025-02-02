package com.example;

import java.math.BigDecimal;

public record Item(String name, BigDecimal price, int discount, int quantity) {
}
