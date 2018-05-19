package com.zingkg.shippinginventorymanager;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Inventory {
    private Map<String, Building> buildings;
    private Map<String, Item> inventory;

    public Inventory(Stream<Building> buildings, Stream<String> inventory, String fileName) {
        this.buildings = buildings.collect(
            Collectors.collectingAndThen(
                Collectors.toMap(Building::getName, Function.identity()),
                Collections::unmodifiableMap
            )
        );
        loadInventory(inventory, fileName);
    }

    public Stream<String> loadInventory(Stream<String> inventoryStream, String fileName) {
        Stream.Builder<String> errors = Stream.builder();
        List<String> inventoryLines = inventoryStream.collect(
            Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList)
        );
        if (inventoryLines.isEmpty())
            return Stream.empty();

        String[] headerTokens = inventoryLines.remove(0).split(",");
        final boolean hasHeader = headerTokens.length >= 3 &&
            headerTokens[0].toLowerCase().equals("item number") &&
            headerTokens[1].toLowerCase().equals("pil") &&
            headerTokens[2].toLowerCase().equals("quantity");
        if (hasHeader) {
            AtomicInteger lineNumber = new AtomicInteger(2);
            inventory = new HashMap<>();
            inventoryLines.stream().map(line -> {
                Optional<Item> item = Optional.empty();
                try {
                    String[] tokens = line.split(",");
                    if (tokens.length < 3) {
                        errors.accept(
                            "Loading " + fileName + " line " + lineNumber.get() + " has errors."
                        );
                        throw new InventoryParseException(
                            "Exception occurred when parsing inventory line."
                        );
                    }
                    Item parsedItem = parseInventoryLine(tokens[0], tokens[1], tokens[2]);
                    if (checkItemLocations(parsedItem)) {
                        item = Optional.of(parsedItem);
                    } else {
                        errors.accept(
                            "Loading " + fileName + " line " + lineNumber.get() + " has an " +
                            "unknown building"
                        );
                    }
                } catch (InventoryParseException e) {
                    errors.accept(
                        "Loading " + fileName + " line " + lineNumber.get() +
                        " has an invalid format."
                    );
                }
                lineNumber.incrementAndGet();
                return item;
            }).filter(Optional::isPresent).map(Optional::get).forEach(item -> {
                // Utilizing a forEach to insert into the inventory hash map due to duplicates
                // causing failure.
                if (inventory.containsKey(item.getItemId())) {
                    errors.accept(
                        "Warning: Loading " + fileName + " item " + item.getItemId() +
                        " already exists in inventory, updating anyways."
                    );
                }
                inventory.put(item.getItemId(), item);
            });
        } else {
            errors.accept(
                "Loaded inventory file " + fileName +
                " does not have header (" + INVENTORY_HEADER + ')'
            );
        }
        return errors.build();
    }

    public Stream<String> updateLocations(Stream<String> inventoryStream, String fileName) {
        Stream.Builder<String> errors = Stream.builder();
        List<String> inventoryLines = inventoryStream.collect(Collectors.toList());
        final int updateTokenLength = 3;
        String[] headerTokens = inventoryLines.remove(0).split(",");
        final boolean hasHeader = headerTokens.length >= updateTokenLength &&
            headerTokens[0].toLowerCase().equals("item number") &&
            headerTokens[1].toLowerCase().equals("operation") &&
            headerTokens[2].toLowerCase().equals("pil");
        if (hasHeader) {
            AtomicInteger lineNumber = new AtomicInteger(2);
            inventoryLines.forEach(line -> {
                // Parse each line and update the entry.
                // Lines go like this: item id, action, PIL
                String[] tokens = line.split(",");
                if (tokens.length < updateTokenLength) {
                    errors.accept(
                        "Updating (" + fileName + ") line " + lineNumber.get() +
                        " does not have the required number of values (" + updateTokenLength + ')'
                    );
                }

                Item item = parseItemLocations(tokens[0], tokens[2]);
                final String operation = tokens[1].toLowerCase();
                if (checkOperationAndItemLocations(operation, item)) {
                    switch (operation) {
                        case "add":
                            // Check item being added to make sure building and spots are valid.
                            if (inventory.containsKey(item.getItemId()))
                                updateInventoryWithLocations(item);
                            else
                                inventory.put(item.getItemId(), item);
                            break;
                        case "delete":
                            inventory.remove(item.getItemId());
                            break;
                        case "update":
                            if (inventory.containsKey(item.getItemId())) {
                                updateInventoryWithLocations(item);
                            } else {
                                errors.accept(
                                    "Updating " + fileName + " line " + lineNumber.get() +
                                    " has invalid item number " + item.getItemId()
                                );
                            }
                            break;
                        default:
                            throw new IllegalArgumentException("Bad action: " + tokens[1]);
                    }
                } else {
                    errors.accept(
                        "Updating " + fileName + " line " + lineNumber.get() +
                        " has an unknown building"
                    );
                }
                lineNumber.incrementAndGet();
            });
        } else {
            errors.accept(
                "Updating inventory file " + fileName +
                " does not have header (" + updateHeader + ')'
            );
        }
        return errors.build();
    }

    private Item updateInventoryWithLocations(Item item) {
        return inventory.put(
            item.getItemId(),
            inventory.get(item.getItemId()).updateLocations(item.getLocations())
        );
    }

    public Stream<String> updateQuantity(Stream<String> inventoryStream, String fileName) {
        Stream.Builder<String> errors = Stream.builder();
        List<String> inventoryLines = inventoryStream.collect(Collectors.toList());
        final int updateTokenLength = 3;
        String[] headerTokens = inventoryLines.remove(0).split(",");
        final boolean hasHeader = headerTokens.length >= updateTokenLength &&
            headerTokens[0].toLowerCase().equals("item number") &&
            headerTokens[1].toLowerCase().equals("operation") &&
            headerTokens[2].toLowerCase().equals("quantity");
        if (hasHeader) {
            AtomicInteger lineNumber = new AtomicInteger(2);
            inventoryLines.forEach(line -> {
                // Parse each line and update the entry.
                // Lines go like this: item id, action, quantity
                String[] tokens = line.split(",");
                if (tokens.length < updateTokenLength) {
                    errors.accept(
                        "Updating " + fileName + " line " + lineNumber.get() +
                        " does not have the required number of values " + updateTokenLength
                    );
                }

                Item item = parseItemQuantity(tokens[0], tokens[2]);
                final String operation = tokens[1].toLowerCase();
                if (checkOperationAndItemLocations(operation, item)) {
                    switch (tokens[1].toLowerCase()) {
                        case "add":
                            // Check item being added to make sure building and spots are valid.
                            if (inventory.containsKey(item.getItemId()))
                                updateInventoryWithQuantity(item);
                            else
                                inventory.put(item.getItemId(), item);
                            break;
                        case "delete":
                            inventory.remove(item.getItemId());
                            break;
                        case "update":
                            if (inventory.containsKey(item.getItemId())) {
                                updateInventoryWithQuantity(item);
                            } else {
                                errors.accept(
                                    "Updating " + fileName + " line " + lineNumber.get() +
                                    " has invalid item number " + item.getItemId()
                                );
                            }
                            break;
                        default:
                            throw new RuntimeException("Bad action: " + tokens[1]);
                    }
                } else {
                    errors.accept(
                        "Updating " + fileName + " line " + lineNumber.get() +
                        " has an unknown building"
                    );
                }
                lineNumber.incrementAndGet();
            });
        } else {
            errors.accept(
                "Updating inventory file " + fileName +
                " does not have headers (" + updateHeader + ')'
            );
        }
        return Stream.empty();
    }

    private Item updateInventoryWithQuantity(Item item) {
        return inventory.put(
            item.getItemId(),
            inventory.get(item.getItemId()).updateQuantity(item.getQuantity())
        );
    }

    private boolean checkItemLocations(Item item) {
        return item.getLocations().allMatch(location ->
            buildings.containsKey(location.getBuilding())
        );
    }

    private boolean checkOperationAndItemLocations(String operation, Item item) {
        return operation.equals("delete") || checkItemLocations(item);
    }

    public Stream<String> saveInventory() {
        Stream<String> headerStream = Stream.of(INVENTORY_HEADER);
        Stream<String> itemStream = inventory.values().stream().map(Item::toCSVRow);
        return Stream.concat(headerStream, itemStream);
    }

    public Stream<String> exportInventory() {
        Stream<String> headerStream = Stream.of(INVENTORY_HEADER);
        Stream<String> itemStream = inventory.keySet().stream().sorted().map(key ->
            inventory.get(key).toCSVRow() + '\n'
        );
        return Stream.concat(headerStream, itemStream);
    }

    public Optional<Item> getItem(String itemId) {
        return Optional.ofNullable(inventory.get(itemId));
    }

    public static final String INVENTORY_HEADER = "item number,pil,quantity";
    private static final String updateHeader = "item number,operation,quantity";

    public static Stream<Building> parseBuildingSetup(File file) throws
        ParserConfigurationException,
        IOException,
        SAXException {
        Stream.Builder<Building> buildings = Stream.builder();
        Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file);
        NodeList nodeList = document.getDocumentElement().getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element elem = (Element) node;
                final String name = elem.getAttribute("name");
                final int aisles = Integer.parseInt(
                    elem.getElementsByTagName(
                        "aisles"
                    ).item(0).getChildNodes().item(0).getNodeValue()
                );
                Stream.Builder<Special> specials = Stream.builder();
                NodeList specialList = elem.getElementsByTagName("special");
                for (int j = 0; j < specialList.getLength(); j++) {
                    Node specialNode = specialList.item(j);
                    if (specialNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element specialElem = (Element) specialNode;
                        final String specialName = specialElem.getAttribute("name");
                        final String specialNick = specialElem.getAttribute("nick");
                        specials.accept(new Special(specialName, specialNick));
                    }
                }
                buildings.accept(new Building(name, aisles, specials.build()));
            }
        }
        return buildings.build();
    }

    private static boolean isOptionalValue(String value) {
        return value.equals("-");
    }

    public static Item parseInventoryLine(String itemId, String pil, String quantityString) {
        Optional<Integer> quantity;
        if (isOptionalValue(quantityString))
            quantity = Optional.empty();
        else
            quantity = Optional.of(Integer.parseInt(quantityString));

        return new Item(
            itemId,
            parseLocationString(pil),
            quantity
        );
    }

    private static Stream<PIL> parseLocationString(String locations) {
        if (isOptionalValue(locations)) {
            return Stream.empty();
        } else {
            return Arrays.stream(
                locations.replaceAll(" Y ", " y ").split("y")
            ).map(location -> {
                String[] locationTokens = location.trim().split("\\.");
                if (locationTokens.length < 3)
                    throw new InventoryParseException("Exception occurred when parsing locations.");
    
                Optional<String> pilLocation;
                if (locationTokens[2].equals("-"))
                    pilLocation = Optional.empty();
                else
                    pilLocation = Optional.of(locationTokens[2]);
    
                return new PIL(
                    locationTokens[0],
                    locationTokens[1],
                    pilLocation
                );
            });
        }
    }

    private static Item parseItemLocations(String itemId, String pil) {
        return new Item(
            itemId,
            parseLocationString(pil),
            Optional.empty()
        );
    }

    private static Item parseItemQuantity(String itemId, String quantity) {
        Optional<Integer> quantityValue;
        if (isOptionalValue(quantity))
            quantityValue = Optional.empty();
        else
            quantityValue = Optional.of(Integer.parseInt(quantity));
        return new Item(itemId, Stream.empty(), quantityValue);
    }
}

class Building {
    private String name;
    private int aisles;
    private List<Special> specials;

    public Building(String name, int aisles, Stream<Special> specials) {
        this.name = name;
        this.aisles = aisles;
        this.specials = specials.collect(Collectors.toList());
    }

    public String getName() {
        return name;
    }

    public int getAisles() {
        return aisles;
    }

    public Stream<Special> getSpecials() {
        return specials.stream();
    }
}

class Special {
    public final String name;
    public final String nick;

    public Special(String name, String nick) {
        this.name = name;
        this.nick = nick;
    }
}
