package com.zingkg.shippinglabelcreator;

import java.awt.Component;
import java.awt.Container;
import java.util.Arrays;

public class TestUtils {
    public static Component getChildNamed(Component parent, final String name) {
        if (name.equals(parent.getName())) {
            return parent;
        } else if (parent instanceof Container) {
            return Arrays.stream(((Container) parent).getComponents()).filter(component ->
                getChildNamed(component, name) != null
            ).findFirst().get();
        } else {
            throw new RuntimeException(
                "Exception occurred when searching parent for component with name"
            );
        }
    }
}
