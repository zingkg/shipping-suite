package com.zingkg.latex;

import org.junit.Assert;
import org.junit.Test;

public class CDataJUnitTest {
    @Test
    public void checkCData() {
        Assert.assertEquals("hi", new CData("hi").data);
        Assert.assertEquals("", new CData("").data);
        Assert.assertEquals(null, new CData(null).data);
    }

    @Test
    public void checkToString() {
        Assert.assertEquals("to string!", new CData("to string!").toString());
        Assert.assertEquals("", new CData("").toString());
        Assert.assertEquals(null, new CData(null).toString());
    }
}
