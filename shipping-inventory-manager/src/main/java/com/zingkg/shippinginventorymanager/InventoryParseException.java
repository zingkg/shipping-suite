package com.zingkg.shippinginventorymanager;

/**
 * Skips the parsing in cases where the inventory format. This is to throw an exception in maps that
 * will be caught later.
 */
public class InventoryParseException extends RuntimeException {
    public InventoryParseException(String message) {
        super(message);
    }
}
