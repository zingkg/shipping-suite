package com.zingkg.latex;

public class CData extends LatexElement {
    public final String data;

    public CData(final String data) {
        this.data = data;
    }

    public String toString() {
        return data;
    }
}
