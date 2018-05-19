package com.zingkg.shippinglabelcreator;

/**
 * An item that is part of an order.
 */
public class Item {
    /**
     * The number of the item.
     */
    public final String itemNo;

    /**
     * The details of the item.
     */
    public final String details;

    /**
     * Notes regarding the item.
     */
    public final String notes;

    /**
     * The number of boxes the item requires.
     */
    public final int boxes;

    /**
     * Default constructor.
     *
     * @param itemNo  The item number.
     * @param details The details of the item.
     * @param notes   The notes regarding the item.
     * @param boxes   The number of boxes the item requires.
     */
    public Item(String itemNo, String details, String notes, int boxes) {
        this.itemNo = itemNo;
        this.details = details;
        this.notes = notes;
        if (boxes > 0)
            this.boxes = boxes;
        else
            this.boxes = 0;
    }

    @Override
    public String toString() {
        String item = "";
        if (!itemNo.isEmpty())
            item = itemNo;
        else if (!details.isEmpty())
            item = details;
        else if (!notes.isEmpty())
            item = notes;

        return item + " - " + Integer.toString(boxes) + " boxes";
    }
}
