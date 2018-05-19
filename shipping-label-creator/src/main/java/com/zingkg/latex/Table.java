package com.zingkg.latex;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Table extends LatexElement {
    private List<LatexElement> elements;

    public Table() {
        elements = new ArrayList<>();
    }

    public void addElement(LatexElement element) {
        elements.add(element);
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
        StringBuffer document = new StringBuffer();
        LatexElement.writeLine(document, "\\begin{tabularx} {\\textwidth} {X l}");
        elements.forEach(element -> LatexElement.writeLine(document, element.toString()));
        LatexElement.writeLine(document, "\\end{tabularx}");
        return document.toString();
    }
}
