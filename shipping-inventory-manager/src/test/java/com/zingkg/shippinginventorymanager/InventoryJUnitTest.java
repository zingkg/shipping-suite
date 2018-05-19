package com.zingkg.shippinginventorymanager;

import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class InventoryJUnitTest {
    private Inventory inventory;

    @Before
    public void setUp() {
        inventory = new Inventory(
            Stream.of(new Building("1", 20, Stream.empty()), new Building("2", 25, Stream.empty())),
            Stream.empty(),
            "inventory.csv"
        );
    }

    private String csvLine(String col1, String col2, String col3) {
        return col1 + ',' + col2 + ',' + col3;
    }

    private Stream<String> inventoryStream(Stream<String> lines) {
        return Stream.concat(Stream.of(Inventory.INVENTORY_HEADER), lines);
    }

    private Stream<String> loadInventory() {
        return inventory.loadInventory(
            inventoryStream(
                Stream.of(
                    csvLine("fp1", "1.-.-", "-"),
                    csvLine("fp2", "1.2.3", "5"),
                    csvLine("fp3", "1.2.2", "5"),
                    csvLine("fp4", "-", "10")
                )
            ),
            "testFile.csv"
        );
    }

    private Stream<String> updateLocationsStream(Stream<String> lines) {
        return Stream.concat(Stream.of("item number,operation,pil"), lines);
    }

    private Stream<String> updateQuantityStream(Stream<String> lines) {
        return Stream.concat(Stream.of("item number,operation,quantity"), lines);
    }

    private String getItemId(String line) {
        String[] tokens = line.split(",");
        if (tokens.length > 0)
            return tokens[0];
        else
            return "";
    }

    @Test
    public void loadInventoryTest() {
        Stream<String> errors = loadInventory();
        assertThat(errors.count(), is(0L));
        Optional<Item> item1 = inventory.getItem("fp1");
        assertThat(item1.isPresent(), is(true));
        assertThat(item1.get().getItemId(), is("fp1"));
        assertThat(inventory.getItem("non-existent").isPresent(), is(false));
        Optional<Item> item2 = inventory.getItem("fp2");
        assertThat(item2.isPresent(), is(true));
        assertThat(item2.get().getItemId(), is("fp2"));
        Optional<Item> item3 = inventory.getItem("fp3");
        assertThat(item3.isPresent(), is(true));
        assertThat(item3.get().getItemId(), is("fp3"));
        Optional<Item> item4 = inventory.getItem("fp4");
        assertThat(item4.isPresent(), is(true));
    }

    @Test
    public void loadInventoryGeneratesErrorsTest() {
        Stream<String> errors = inventory.loadInventory(
            inventoryStream(
                Stream.of(
                    csvLine("fp1", "1", "-"),
                    csvLine("fp2", "1.1", "-")
                )
            ),
            "testFile.csv"
        );
        assertThat(errors.count(), is(2L));
    }

    @Test
    public void updateLocationsTest() {
        loadInventory();
        Stream<String> errors = inventory.updateLocations(
            updateLocationsStream(
                Stream.of(
                    csvLine("fp1", "update", "1.1.1"),
                    csvLine("fp2", "delete", "-.-.-"),
                    csvLine("fp4", "add", "2.2.2"),
                    csvLine("fp3", "add", "2.1.1")
                )
            ),
            "updateLocations.csv"
        );
        assertThat(errors.count(), is(0L));
        assertThat(inventory.getItem("fp1").isPresent(), is(true));
        assertThat(inventory.getItem("fp2").isPresent(), is(false));
        assertThat(inventory.getItem("fp3").isPresent(), is(true));
        assertThat(inventory.getItem("fp4").isPresent(), is(true));
        Item fp1 = inventory.getItem("fp1").get();
        assertThat(fp1.getQuantity().isPresent(), is(false));
        List<PIL> fp1Locations = fp1.getLocations().collect(
            Collectors.collectingAndThen(
                Collectors.toList(),
                Collections::unmodifiableList
            )
        );
        assertThat(fp1Locations.size(), is(1));
        PIL fp1Location = fp1Locations.get(0);
        assertThat(fp1Location.getBuilding(), is("1"));
        assertThat(fp1Location.getAisle(), is("1"));
        assertThat(fp1Location.getPIL().isPresent(), is(true));
        assertThat(fp1Location.getPIL().get(), is("1"));

        Item fp3 = inventory.getItem("fp3").get();
        assertThat(fp3.getQuantity().isPresent(), is(true));
        assertThat(fp3.getQuantity().get(), is(5));
    }

    @Test
    public void updateLocationsErrorsTest() {
        loadInventory();
        Stream<String> errors = inventory.updateLocations(
            updateLocationsStream(
                Stream.of(
                    csvLine("fp5", "update", "1.1.1"),
                    csvLine("fp1", "update", "unknown.1.1")
                )
            ),
            "updateLocationsError.csv"
        );
        assertThat(errors.count(), is(2L));
    }

    @Test
    public void updateQuantityTest() {
        loadInventory();
        Stream<String> errors = inventory.updateQuantity(
            updateQuantityStream(
                Stream.of(
                    csvLine("fp1", "update", "2"),
                    csvLine("fp2", "delete", "-"),
                    csvLine("fp3", "update", "-"),
                    csvLine("fp4", "add", "15")
                )
            ),
            "updateQuantity.csv"
        );
        assertThat(errors.count(), is(0L));
        assertThat(inventory.getItem("fp1").isPresent(), is(true));
        assertThat(inventory.getItem("fp2").isPresent(), is(false));
        assertThat(inventory.getItem("fp3").isPresent(), is(true));
        assertThat(inventory.getItem("fp4").isPresent(), is(true));
        Item fp1 = inventory.getItem("fp1").get();
        assertThat(fp1.getQuantity().isPresent(), is(true));
        assertThat(fp1.getItemId(), is("fp1"));
        assertThat(inventory.getItem("fp3").get().getQuantity().isPresent(), is(false));
        Item fp4 = inventory.getItem("fp4").get();
        assertThat(fp4.getQuantity().isPresent(), is(true));
        assertThat(fp4.getQuantity().get(), is(15));
        assertThat(fp4.getLocations().count(), is(0L));
    }

    @Test
    public void saveInventoryTest() {
        loadInventory();
        List<String> inventoryLines = inventory.saveInventory().collect(
            Collectors.collectingAndThen(
                Collectors.toList(),
                Collections::unmodifiableList
            )
        );
        assertThat(inventoryLines.get(0), is(Inventory.INVENTORY_HEADER));
        assertThat(inventoryLines.size(), is(5));
    }

    @Test
    public void exportInventoryTest() {
        inventory.loadInventory(
            inventoryStream(
                Stream.of(
                    csvLine("fp5", "1.1.1", "2"),
                    csvLine("fp9", "1.1.2", "3"),
                    csvLine("fp8", "1.1.5", "5"),
                    csvLine("fp1", "1.2.2", "-")
                )
            ),
            "outOfOrderInventory.csv"
        );
        List<String> inventoryLines = inventory.exportInventory().collect(
            Collectors.collectingAndThen(
                Collectors.toList(),
                Collections::unmodifiableList
            )
        );
        assertThat(inventoryLines.size(), is(5));
        assertThat(inventoryLines.get(0), is(Inventory.INVENTORY_HEADER));
        assertThat(getItemId(inventoryLines.get(1)), is("fp1"));
        assertThat(getItemId(inventoryLines.get(2)), is("fp5"));
        assertThat(getItemId(inventoryLines.get(3)), is("fp8"));
        assertThat(getItemId(inventoryLines.get(4)), is("fp9"));
    }
}
