package com.zingkg.latex;

public abstract class LatexElement {
    /**
     * Writes a line to the string buffer.
     *
     * @param document The string buffer to be written to.
     * @param text     The text to write to the buffer.
     */
    public static void writeLine(StringBuffer document, final String text) {
        document.append(text);
        document.append("\n");
    }
}
