package com.zingkg.shippinglabelcreator;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * An order is the destination.
 */
public class Order {
    /**
     * The name of the destination.
     */
    private final String name;

    /**
     * The address of the destination.
     */
    private final String address;

    /**
     * The city of the destination.
     */
    private final String city;

    /**
     * The zip code of the destination.
     */
    private final String zipCode;

    /**
     * The items associated with the order.
     */
    private final List<Item> items;

    /**
     * The PO box number of the destination.
     */
    private final String po;

    /**
     * The department number of the destination.
     */
    private final String deptNo;

    /**
     * Default constructor.
     *
     * @param name    The name of the destination.
     * @param address The address of the order.
     * @param city    The order's city.
     * @param zipCode The zip code of the order.
     * @param items   The items that compose the order.
     * @param po      The po box of the order (optional)
     * @param deptNo  The dept number of the order (optional)
     */
    public Order(
        final String name,
        final String address,
        final String city,
        final String zipCode,
        List<Item> items,
        final String po,
        final String deptNo
    ) {
        this.name = name;
        this.address = address;
        this.city = city;
        this.zipCode = zipCode;
        this.items = items;
        this.po = po;
        this.deptNo = deptNo;
    }

    /**
     * Gets the name of the destination.
     *
     * @return The name of the destination.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the address associated with the order.
     *
     * @return The order's address.
     */
    public String getAddress() {
        return address;
    }

    /**
     * Gets the destination city for this order.
     *
     * @return The destination's city.
     */
    public String getCity() {
        return city;
    }

    /**
     * Gets the zip code of the destination.
     *
     * @return The zip code of the destination.
     */
    public String getZipCode() {
        return zipCode;
    }

    /**
     * Gets the items associated with this order.
     *
     * @return The items associated with the order.
     */
    public Stream<Item> getItems() {
        return items.stream();
    }

    /**
     * Gets the PO box number for this order.
     *
     * @return The PO box number of this order.
     */
    public String getPO() {
        return po;
    }

    /**
     * Gets the department number for this order.
     *
     * @return The department number.
     */
    public String getDeptNo() {
        return deptNo;
    }

    public static final class Builder {
        /**
         * The name of the destination.
         */
        private String name;

        /**
         * The address of the destination.
         */
        private String address;

        /**
         * The city of the destination.
         */
        private String city;

        /**
         * The zip code of the destination.
         */
        private String zipCode;

        /**
         * The items associated with the order.
         */
        private List<Item> items;

        /**
         * The PO box number of the destination.
         */
        private String po;

        /**
         * The department number of the destination.
         */
        private String deptNo;

        /**
         * Sets the name of the destination.
         *
         * @param name The name to be set.
         */
        public void setName(final String name) {
            this.name = name;
        }

        /**
         * Sets the address for this order.
         *
         * @param address The address to be set onto this order.
         */
        public void setAddress(final String address) {
            this.address = address;
        }

        /**
         * Sets the destination's city for this order.
         *
         * @param city The destination's city.
         */
        public void setCity(final String city) {
            this.city = city;
        }

        /**
         * Sets the zip code of the destination.
         *
         * @param zipCode The zip code of the destination.
         */
        public void setZipCode(final String zipCode) {
            this.zipCode = zipCode;
        }

       /**
        * Sets the items to be associated with this order.
        *
        * @param items The items to be associated with this order.
        */
       public void setItems(Stream<Item> items) {
           this.items = items.collect(
               Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList)
           );
       }

       /**
        * Sets the PO box number for this order.
        *
        * @param po The PO box to be used for this order.
        */
       public void setPO(final String po) {
           this.po = po;
       }

       /**
        * Sets the department number for this order.
        *
        * @param deptNo The department number to be set for this order.
        */
       public void setDeptNo(String deptNo) {
           this.deptNo = deptNo;
       }

       /**
        * Builds the order from the given inputs. This is so the order data structure is immutable.
        *
        * @return The order constructed from the builder.
        */
       public Order build() {
           return new Order(name, address, city, zipCode, items, po, deptNo);
       }
    }
}
