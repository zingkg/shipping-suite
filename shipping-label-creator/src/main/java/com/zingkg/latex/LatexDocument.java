package com.zingkg.latex;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LatexDocument {
    private StringBuffer document;
    private List<LatexElement> elements;
    private int fontSize;

    public LatexDocument(final int fontSize) {
        document = new StringBuffer();
        elements = new ArrayList<>();
        setFontSize(fontSize);
        // Writer headers.
        writeLine("\\documentclass{article}");
        writeLine("\\usepackage{tabularx}");
        writeLine("\\usepackage[top=0.1cm, bottom=0.1cm, left=0.2cm, right=0.2cm]{geometry}");
        writeLine("\\setlength{\\tabcolsep}{15pt}");
        writeLine("\\begin{document}");
    }

    public void setFontSize(final int fontSize) {
        this.fontSize = fontSize;
    }

    public void addAllElements(Stream<LatexElement> stream) {
        elements.addAll(
            stream.collect(
                Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList)
            )
        );
    }

    @Override
    public String toString() {
        writeLine("{\\fontsize{" + fontSize + "}{" + (fontSize / 4) + "}");
        writeLine("\\selectfont");
        // Write LaTeX elements.
        elements.forEach(element -> writeLine(element.toString()));
        // We are done with it. Generate latex end.
        writeLine("}");
        writeLine("\\end{document}");
        return document.toString();
    }

    /**
     * Writes a line to the buffered writer.
     *
     * @param text   The text to write to the buffer.
     */
    private void writeLine(final String text) {
        LatexElement.writeLine(document, text);
    }
}
