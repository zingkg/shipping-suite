package com.zingkg.shippinginventorymanager;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Item {
    private String itemId;
    private List<PIL> locations;
    private Optional<Integer> quantity;

    public Item(String itemId, Stream<PIL> locations, Optional<Integer> quantity) {
        this.itemId = itemId;
        this.locations = locations.collect(
            Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList)
        );
        this.quantity = quantity;
    }

    public String toCSVRow() {
        StringJoiner locationString = new StringJoiner(" Y ");
        if (locations.isEmpty()) {
            locationString.add("-");
        } else {
            locations.stream().forEach(location -> {
                String pil;
                if (location.getPIL().isPresent())
                    pil = location.getPIL().get();
                else
                    pil = "-";
    
                locationString.add(location.getBuilding() + '.' + location.getAisle() + '.' + pil);
            });
        }
        final String quantityString;
        if (quantity.isPresent())
            quantityString = quantity.get().toString();
        else
            quantityString = "-";

        return itemId + ',' + locationString.toString() + ',' + quantityString;
    }

    public String getItemId() {
        return itemId;
    }

    public Stream<PIL> getLocations() {
        return locations.stream();
    }

    public Optional<Integer> getQuantity() {
        return quantity;
    }

    public Item updateLocations(Stream<PIL> updatedLocations) {
        return new Item(itemId, updatedLocations, quantity);
    }

    public Item updateQuantity(Optional<Integer> updatedQuantity) {
        return new Item(itemId, locations.stream(), updatedQuantity);
    }
}

/**
 * Physical Inventory Location (PIL)
 */
class PIL {
    private String building;
    private String aisle;
    private Optional<String> pil;

    public PIL(String building, String aisle, Optional<String> pil) {
        this.building = building;
        this.aisle = aisle;
        this.pil = pil;
    }

    public String getBuilding() {
        return building;
    }

    public String getAisle() {
        return aisle;
    }

    public Optional<String> getPIL() {
        return pil;
    }
}
