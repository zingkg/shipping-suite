package com.zingkg.latex;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Section extends LatexElement {
    private List<LatexElement> elements;
    private boolean shouldAddEndingLine;

    public Section(boolean shouldAddEndingLine) {
        elements = new ArrayList<>();
        this.shouldAddEndingLine = shouldAddEndingLine;
    }

    public void addElement(LatexElement e) {
        elements.add(e);
    }

    public void addAllElements(Stream<LatexElement> elements) {
        this.elements.addAll(
            elements.collect(
                Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList)
            )
        );
    }

    @Override
    public String toString() {
        StringBuffer document = new StringBuffer();
        // Begin section and begin table.
        writeLine(document, "\\vbox{");
        writeLine(document, "\\begin{center}");
        elements.forEach(element -> writeLine(document, element.toString()));
        if (shouldAddEndingLine)
            writeLine(document, "\\noindent\\makebox[\\linewidth]{\\rule{\\paperwidth}{0.4pt}}");

        writeLine(document, "\\end{center}");
        writeLine(document, "}");
        return document.toString();
    }
}
