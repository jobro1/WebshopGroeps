package com.luxuryproductsholding.api.exceptions;

public class insufficientStockException extends RuntimeException {
    public insufficientStockException(String message) {
        super(message);
    }
}
