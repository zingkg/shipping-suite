package com.zingkg.shippinginventorymanager;

import org.junit.Test;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class ItemJUnitTest {
    @Test
    public void toCSVRowTest() {
        String csvRow = new Item(
            "fp1",
            Stream.of(new PIL("b1", "2", Optional.of("12")), new PIL("b1", "2", Optional.of("14"))),
            Optional.empty()
        ).toCSVRow();
        assertThat(csvRow, is("fp1,b1.2.12 Y b1.2.14,-"));

        String csvRow2 = new Item(
            "fp2",
            Stream.empty(),
            Optional.of(2)
        ).toCSVRow();
        assertThat(csvRow2, is("fp2,-,2"));

        String csvRow3 = new Item(
            "fp3",
            Stream.of(new PIL("b2", "2", Optional.of("12"))),
            Optional.empty()
        ).toCSVRow();
        assertThat(csvRow3, is("fp3,b2.2.12,-"));
    }

    @Test
    public void updateLocationsTest() {
        Item item = new Item("fp1", Stream.empty(), Optional.of(3));
        Item updatedItem = item.updateLocations(Stream.of(new PIL("b1", "2", Optional.empty())));
        assertThat(updatedItem.getItemId(), is(item.getItemId()));
        List<PIL> updatedItemLocations = updatedItem.getLocations().collect(
            Collectors.collectingAndThen(
                Collectors.toList(),
                Collections::unmodifiableList
            )
        );
        assertThat(updatedItemLocations.size(), is(1));
        assertThat(updatedItemLocations.get(0).getBuilding(), is("b1"));
        assertThat(updatedItemLocations.get(0).getAisle(), is("2"));
        assertThat(updatedItemLocations.get(0).getPIL().isPresent(), is(false));
        assertThat(updatedItem.getQuantity().get(), is(3));
        assertThat(item.getLocations().count(), is(0L));
    }

    @Test
    public void updateQuantityTest() {
        Item item = new Item("fp1", Stream.empty(), Optional.empty());
        Item updatedItem = item.updateQuantity(Optional.of(5));
        assertThat(updatedItem.getItemId(), is(item.getItemId()));
        assertThat(updatedItem.getLocations().count(), is(0L));
        assertThat(updatedItem.getQuantity().isPresent(), is(true));
        assertThat(updatedItem.getQuantity().get(), is(5));
        assertThat(item.getItemId(), is("fp1"));
        assertThat(item.getLocations().count(), is(0L));
        assertThat(item.getQuantity().isPresent(), is(false));
    }
}
