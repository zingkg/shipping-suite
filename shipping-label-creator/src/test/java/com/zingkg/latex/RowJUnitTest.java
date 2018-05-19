package com.zingkg.latex;

import org.junit.Assert;
import org.junit.Test;

public class RowJUnitTest {
    @Test
    public void checkRow() {
        Row empty = new Row("", "");
        Assert.assertEquals("", empty.column1);
        Assert.assertEquals("", empty.column2);

        Row row1 = new Row("row1, column1", "row1, column2");
        Assert.assertEquals("row1, column1", row1.column1);
        Assert.assertEquals("row1, column2", row1.column2);

        Row nullRow = new Row(null, null);
        Assert.assertEquals(null, nullRow.column1);
        Assert.assertEquals(null, nullRow.column2);
    }

    @Test
    public void checkToString() {
        Row empty = new Row("", "");
        Assert.assertEquals("\\multicolumn{1}{l}{} & \\multicolumn{1}{l}{}\\\\", empty.toString());

        Row row1 = new Row("filled home", "filled sent");
        Assert.assertEquals(
            "\\multicolumn{1}{l}{filled home} & \\multicolumn{1}{l}{filled sent}\\\\",
            row1.toString()
        );

        Row nullRow = new Row(null, null);
        Assert.assertEquals(
            "\\multicolumn{1}{l}{null} & \\multicolumn{1}{l}{null}\\\\",
            nullRow.toString()
        );
    }
}
