package com.zingkg.shippinglabelcreator;

import java.awt.Component;
import java.io.File;
import javax.swing.*;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * The GUI test class will test the user interface (UI) and all of its components. This class will
 * ensure that the UI is correct and to ensure that it operates correctly.
 */
public class GUIJUnitTest {
    private MainWindow mainWindow;

    /**
     * This executes tasks before each test. This will initialize the main window.
     */
    @Before
    public void beforeTest() {
        mainWindow = new MainWindow(new Settings("", "", "", "", "", "", false));
        mainWindow.setVisible(true);
    }

    /**
     * This executes tasks after each test. This will delete any files that may have been created.
     */
    @After
    public void afterTest() {
        File pdfFile = new File("testfile.pdf");
        File texFile = new File("testfile.tex");

        if (pdfFile.exists())
            pdfFile.delete();

        if (texFile.exists())
            texFile.delete();
    }

    /**
     * Checks the UI to make sure that it is valid. This is also to check the names for each of the
     * components so that later we can use them.
     */
    @Test
    public void checkUiTest() {
        checkComponentExists("orderNameText", "Order's name text field must appear");
        checkComponentExists("orderAddressText", "Order's address text field must appear");
        checkComponentExists("orderCityText", "Order's city text field must appear");
        checkComponentExists("orderZipCodeText", "Order's zip code text field must appear");
        checkComponentExists("orderPoText", "Order's po text field must appear");
        checkComponentExists("orderDeptText", "Order's dept text field must appear");
        checkComponentExists("itemNoText", "Item's number text field must appear");
        checkComponentExists("itemDetailsText", "Item's detail text field must appear");
        checkComponentExists("itemNotesText", "Item's notes text field must appear");
        checkComponentExists("itemBoxesText", "Item's boxes text field must appear");
        checkComponentExists("orderItemList", "Order item list must appear");
        checkComponentExists("addButton", "Add button must appear");
        checkComponentExists("loadItemButton", "Load item button must appear");
        checkComponentExists("clearItemButton", "Clear item button must appear");
        checkComponentExists("createButton", "Create button must appear");
        checkComponentExists("removeButton", "Remove button must appear");
        checkComponentExists("textSizeSpinner", "Text size spinner must appear");
    }

    /**
     * Checks to see if a component given its name exists.
     *
     * @param name          The name assigned to the component.
     * @param assertMessage The message to return to the user if the component was not found.
     */
    private void checkComponentExists(final String name, final String assertMessage) {
        Component component = TestUtils.getChildNamed(mainWindow, name);
        Assert.assertNotNull(assertMessage, component);
    }

    /**
     * A standard example test that was given by the client.
     */
    @Test
    public void standardTest() {
        JTextField orderNameText = (JTextField) TestUtils.getChildNamed(
            mainWindow,
            "orderNameText"
        );
        JTextPane orderAddressText = (JTextPane) TestUtils.getChildNamed(
            mainWindow,
            "orderAddressText"
        );
        JTextField orderCityText = (JTextField) TestUtils.getChildNamed(
            mainWindow,
            "orderCityText"
        );
        JTextField orderZipCodeText = (JTextField) TestUtils.getChildNamed(
            mainWindow,
            "orderZipCodeText"
        );
        JTextField orderPoText = (JTextField) TestUtils.getChildNamed(mainWindow, "orderPoText");
        JTextField orderDeptText = (JTextField) TestUtils.getChildNamed(
            mainWindow,
            "orderDeptText"
        );
        JList orderItemList = (JList) TestUtils.getChildNamed(mainWindow, "orderItemList");
        JTextField itemNoText = (JTextField) TestUtils.getChildNamed(mainWindow, "itemNoText");
        JTextField itemDetailsText = (JTextField) TestUtils.getChildNamed(
            mainWindow,
            "itemDetailsText"
        );
        JTextField itemNotesText = (JTextField) TestUtils.getChildNamed(
            mainWindow,
            "itemNotesText");
        JTextField itemBoxesText = (JTextField) TestUtils.getChildNamed(
            mainWindow,
            "itemBoxesText"
        );
        JButton addButton = (JButton) TestUtils.getChildNamed(mainWindow, "addButton");
        JButton createButton = (JButton) TestUtils.getChildNamed(mainWindow, "createButton");

        orderNameText.setText("Homesense Dist ctr");
        orderAddressText.setText("3185 American Dr.");
        orderCityText.setText("Missisagua, Ontario");
        orderZipCodeText.setText("L4V 1B8");
        orderPoText.setText("25741871");
        orderDeptText.setText("75");

        // Add an item and check if the list model has that item
        itemNoText.setText("FP-3985-3BLG(sm-md-Lg)");
        itemDetailsText.setText("6pcs of each size per cs/pk");
        itemNotesText.setText("Total 18 pcs");
        itemBoxesText.setText("14");
        addButton.doClick();
        Assert.assertEquals(1, orderItemList.getModel().getSize());
        Assert.assertEquals(1, mainWindow.getItems().count());

        // Add an item and check if the list model has that item.
        itemNoText.setText("FP-3993-2W(sm-Lg)");
        itemDetailsText.setText("6 pcs of each size per cs/pk");
        itemNotesText.setText("Total 12 pcs");
        itemBoxesText.setText("15");
        addButton.doClick();
        Assert.assertEquals(2, orderItemList.getModel().getSize());
        Assert.assertEquals(2, mainWindow.getItems().count());

        // Create a pdf and verify there are no errors.
        createButton.doClick();
        // TODO: Check if dialog comes up now
    }

    /**
     * Removes some items in the item list and checks to make sure they were removed.
     */
    @Test
    public void removeItemTest() {
        JTextField orderNameText = (JTextField) TestUtils.getChildNamed(
            mainWindow,
            "orderNameText"
        );
        JTextPane orderAddressText = (JTextPane) TestUtils.getChildNamed(
            mainWindow,
            "orderAddressText"
        );
        JTextField orderCityText = (JTextField) TestUtils.getChildNamed(
            mainWindow,
            "orderCityText"
        );
        JTextField orderZipCodeText = (JTextField) TestUtils.getChildNamed(
            mainWindow,
            "orderZipCodeText"
        );
        JTextField orderPoText = (JTextField) TestUtils.getChildNamed(mainWindow, "orderPoText");
        JTextField orderDeptText = (JTextField) TestUtils.getChildNamed(
            mainWindow,
            "orderDeptText"
        );
        JList orderItemList = (JList) TestUtils.getChildNamed(mainWindow, "orderItemList");
        JTextField itemNoText = (JTextField) TestUtils.getChildNamed(mainWindow, "itemNoText");
        JTextField itemDetailsText = (JTextField) TestUtils.getChildNamed(
            mainWindow,
            "itemDetailsText"
        );
        JTextField itemNotesText = (JTextField) TestUtils.getChildNamed(
            mainWindow,
            "itemNotesText"
        );
        JTextField itemBoxesText = (JTextField) TestUtils.getChildNamed(
            mainWindow,
            "itemBoxesText"
        );
        JButton addButton = (JButton) TestUtils.getChildNamed(mainWindow, "addButton");
        JButton removeButton = (JButton) TestUtils.getChildNamed(mainWindow, "removeButton");

        orderNameText.setText("Homesense Dist ctr");
        orderAddressText.setText("3185 American Dr.");
        orderCityText.setText("Missisagua, Ontario");
        orderZipCodeText.setText("L4V 1B8");
        orderPoText.setText("25741871");
        orderDeptText.setText("75");

        // Add an item and check if the list model has that item
        itemNoText.setText("FP-3985-3BLG(sm-md-Lg)");
        itemDetailsText.setText("6pcs of each size per cs/pk");
        itemNotesText.setText("Total 18 pcs");
        itemBoxesText.setText("14");
        addButton.doClick();
        Assert.assertEquals(1, orderItemList.getModel().getSize());
        Assert.assertEquals(1, mainWindow.getItems().count());

        // Add an item and check if the list model has that item
        itemNoText.setText("FP-3993-2W(sm-Lg)");
        itemDetailsText.setText("6 pcs of each size per cs/pk");
        itemNotesText.setText("Total 12 pcs");
        itemBoxesText.setText("15");
        addButton.doClick();
        Assert.assertEquals(2, orderItemList.getModel().getSize());
        Assert.assertEquals(2, mainWindow.getItems().count());

        orderItemList.setSelectedIndex(0);
        removeButton.doClick();
        Assert.assertEquals(1, orderItemList.getModel().getSize());
        Assert.assertEquals(1, mainWindow.getItems().count());

        orderItemList.setSelectedIndex(0);
        removeButton.doClick();
        Assert.assertEquals(0, orderItemList.getModel().getSize());
        Assert.assertEquals(0, mainWindow.getItems().count());
    }

    @Test
    public void zipCodeTextSpinnerBugTest() {
        JTextField orderZipCodeText = (JTextField) TestUtils.getChildNamed(
            mainWindow,
            "orderZipCodeText"
        );
        JSpinner textSizeSpinner = (JSpinner) TestUtils.getChildNamed(
            mainWindow,
            "textSizeSpinner"
        );

        orderZipCodeText.setText("2 43 5 6 7");
        textSizeSpinner.setValue(15);
        Assert.assertEquals(15, textSizeSpinner.getValue());
    }

    @Test
    public void addBadItemBoxesTest() {
        JList orderItemList = (JList) TestUtils.getChildNamed(mainWindow, "orderItemList");
        JTextField itemNoText = (JTextField) TestUtils.getChildNamed(mainWindow, "itemNoText");
        JTextField itemDetailsText = (JTextField) TestUtils.getChildNamed(
            mainWindow,
            "itemDetailsText"
        );
        JTextField itemNotesText = (JTextField) TestUtils.getChildNamed(
            mainWindow,
            "itemNotesText"
        );
        JTextField itemBoxesText = (JTextField) TestUtils.getChildNamed(
            mainWindow,
            "itemBoxesText"
        );
        JButton addButton = (JButton) TestUtils.getChildNamed(mainWindow, "addButton");

        // Add an item and check if the list model has that item
        itemNoText.setText("FP-3985-3BLG(sm-md-Lg)");
        itemDetailsText.setText("6pcs of each size per cs/pk");
        itemNotesText.setText("Total 18 pcs");
        itemBoxesText.setText("-1");
        addButton.doClick();
        Assert.assertEquals(0, orderItemList.getModel().getSize());
        Assert.assertEquals(0, mainWindow.getItems().count());
    }

    @Test
    public void poSpaceTest() {
        JTextField orderNameText = (JTextField) TestUtils.getChildNamed(
            mainWindow,
            "orderNameText"
        );
        JTextPane orderAddressText = (JTextPane) TestUtils.getChildNamed(
            mainWindow,
            "orderAddressText"
        );
        JTextField orderCityText = (JTextField) TestUtils.getChildNamed(
            mainWindow,
            "orderCityText"
        );
        JTextField orderZipCodeText = (JTextField) TestUtils.getChildNamed(
            mainWindow,
            "orderZipCodeText"
        );
        JTextField orderPoText = (JTextField) TestUtils.getChildNamed(mainWindow, "orderPoText");
        JTextField orderDeptText = (JTextField) TestUtils.getChildNamed(
            mainWindow,
            "orderDeptText"
        );
        JList orderItemList = (JList) TestUtils.getChildNamed(mainWindow, "orderItemList");
        JTextField itemNoText = (JTextField) TestUtils.getChildNamed(mainWindow, "itemNoText");
        JTextField itemDetailsText = (JTextField) TestUtils.getChildNamed(
            mainWindow,
            "itemDetailsText"
        );
        JTextField itemNotesText = (JTextField) TestUtils.getChildNamed(
            mainWindow,
            "itemNotesText"
        );
        JTextField itemBoxesText = (JTextField) TestUtils.getChildNamed(
            mainWindow,
            "itemBoxesText"
        );
        JButton addButton = (JButton) TestUtils.getChildNamed(mainWindow, "addButton");
        JButton createButton = (JButton) TestUtils.getChildNamed(mainWindow, "createButton");

        orderNameText.setText("Homesense Dist ctr");
        orderAddressText.setText("3185 American Dr.");
        orderCityText.setText("Missisagua, Ontario");
        orderZipCodeText.setText("L4V 1B8");
        orderPoText.setText("25 74 18 71");
        orderDeptText.setText("75");

        // Add an item and check if the list model has that item
        itemNoText.setText("FP-3985-3BLG(sm-md-Lg)");
        itemDetailsText.setText("6pcs of each size per cs/pk");
        itemNotesText.setText("Total 18 pcs");
        itemBoxesText.setText("14");
        addButton.doClick();
        Assert.assertEquals(1, orderItemList.getModel().getSize());
        Assert.assertEquals(1, mainWindow.getItems().count());

        // Add an item and check if the list model has that item
        itemNoText.setText("FP-3993-2W(sm-Lg)");
        itemDetailsText.setText("6 pcs of each size per cs/pk");
        itemNotesText.setText("Total 12 pcs");
        itemBoxesText.setText("15");
        addButton.doClick();
        Assert.assertEquals(2, orderItemList.getModel().getSize());
        Assert.assertEquals(2, mainWindow.getItems().count());

        // Create a pdf and verify there are no errors
        createButton.doClick();
        // TODO: Check dialog box and hit save
    }

    @Test
    public void boxSpaceTest() {
        JTextField orderNameText = (JTextField) TestUtils.getChildNamed(
            mainWindow,
            "orderNameText"
        );
        JTextPane orderAddressText = (JTextPane) TestUtils.getChildNamed(
            mainWindow,
            "orderAddressText"
        );
        JTextField orderCityText = (JTextField) TestUtils.getChildNamed(
            mainWindow,
            "orderCityText"
        );
        JTextField orderZipCodeText = (JTextField) TestUtils.getChildNamed(
            mainWindow,
            "orderZipCodeText"
        );
        JTextField orderPoText = (JTextField) TestUtils.getChildNamed(mainWindow, "orderPoText");
        JTextField orderDeptText = (JTextField) TestUtils.getChildNamed(
            mainWindow,
            "orderDeptText"
        );
        JList orderItemList = (JList) TestUtils.getChildNamed(mainWindow, "orderItemList");
        JTextField itemNoText = (JTextField) TestUtils.getChildNamed(mainWindow, "itemNoText");
        JTextField itemDetailsText = (JTextField) TestUtils.getChildNamed(
            mainWindow,
            "itemDetailsText"
        );
        JTextField itemNotesText = (JTextField) TestUtils.getChildNamed(
            mainWindow,
            "itemNotesText"
        );
        JTextField itemBoxesText = (JTextField) TestUtils.getChildNamed(
            mainWindow,
            "itemBoxesText"
        );
        JButton addButton = (JButton) TestUtils.getChildNamed(mainWindow, "addButton");
        JButton createButton = (JButton) TestUtils.getChildNamed(mainWindow, "createButton");

        orderNameText.setText("Homesense Dist ctr");
        orderAddressText.setText("3185 American Dr.");
        orderCityText.setText("Missisagua, Ontario");
        orderZipCodeText.setText("L4V 1B8");
        orderPoText.setText("25741871");
        orderDeptText.setText("75");

        // Add an item and check if the list model has that item
        itemNoText.setText("FP-3985-3BLG(sm-md-Lg)");
        itemDetailsText.setText("6pcs of each size per cs/pk");
        itemNotesText.setText("Total 18 pcs");
        itemBoxesText.setText(" 1 4 ");
        addButton.doClick();
        Assert.assertEquals(1, orderItemList.getModel().getSize());
        Assert.assertEquals(1, mainWindow.getItems().count());

        // Add an item and check if the list model has that item
        itemNoText.setText("FP-3993-2W(sm-Lg)");
        itemDetailsText.setText("6 pcs of each size per cs/pk");
        itemNotesText.setText("Total 12 pcs");
        itemBoxesText.setText("  1   5  ");
        addButton.doClick();
        Assert.assertEquals(2, orderItemList.getModel().getSize());
        Assert.assertEquals(2, mainWindow.getItems().count());

        // Create a pdf and verify there are no errors
        createButton.doClick();
        // TODO: Check for dialog box
    }
}
