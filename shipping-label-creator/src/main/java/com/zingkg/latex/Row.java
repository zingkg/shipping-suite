package com.zingkg.latex;

public class Row extends LatexElement {
    public final String column1;
    public final String column2;

    public Row(final String column1, final String column2) {
        this.column1 = column1;
        this.column2 = column2;
    }

    @Override
    public String toString() {
        return "\\multicolumn{1}{l}{" + column1 + "} & \\multicolumn{1}{l}{" + column2 + "}\\\\";
    }
}
