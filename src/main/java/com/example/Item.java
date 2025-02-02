package com.example;

import java.math.BigDecimal;

public record Item(int barcode, String name, BigDecimal price, int discount, int quantity) {
}
