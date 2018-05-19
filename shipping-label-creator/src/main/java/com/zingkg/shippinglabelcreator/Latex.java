package com.zingkg.shippinglabelcreator;

import com.zingkg.latex.CData;
import com.zingkg.latex.LatexDocument;
import com.zingkg.latex.LatexElement;
import com.zingkg.latex.Row;
import com.zingkg.latex.Section;
import com.zingkg.latex.Table;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * This class contains static functions that will write LaTeX to a file to be turned into a PDF
 * later.
 */
public class Latex {
    public static boolean generateLatex(
        Settings settings,
        String fileName,
        Order order,
        final int fontSize
    ) {
        try (Writer writer = new BufferedWriter(new FileWriter(fileName))) {
            LatexDocument document = new LatexDocument(fontSize);
            order.getItems().forEachOrdered(item -> {
                Stream<LatexElement> sections = IntStream.range(1, item.boxes).mapToObj(i -> {
                    Section section = new Section(settings.shouldAddEntryLine);
                    section.addElement(createTableContents(settings, order, item));
                    section.addAllElements(
                        createPOAndDept(order.getPO(), order.getDeptNo(), i, item.boxes)
                    );
                    return section;
                });
                document.addAllElements(sections);
            });
            writer.write(document.toString());
            return true;
        } catch (IOException ex) {
            System.err.println("IOException when writing: " + ex);
        }
        return false;
    }

    private static Table createTableContents(Settings settings, Order order, Item item) {
        Table table = new Table();
        String[] destinations = order.getAddress().split("\n");
        table.addElement(new CData(settings.homeName + " & " + order.getName() + "\\\\"));
        table.addElement(new Row(settings.homeAddress, destinations[0]));
        table.addAllElements(
            IntStream.range(1, destinations.length).mapToObj(i -> new Row("", destinations[i]))
        );

        table.addElement(
            new CData(
                settings.homeCity + ", " + settings.homeState + ' ' +
                settings.homeZipCode + " & " + order.getCity() + ' ' + order.getZipCode() +
                "\\\\"
            )
        );
        table.addElement(new CData("Item \\#" + item.itemNo + " & " + item.details + "\\\\"));
        if (item.notes != null && !item.notes.isEmpty())
            table.addElement(new CData('&' + item.notes + "\\\\"));

        return table;
    }

    private static Stream<LatexElement> createPOAndDept(
        final String po,
        final String deptNo,
        final int boxNo,
        final int maxBox
    ) {
        Stream.Builder<LatexElement> elements = Stream.builder();
        elements.add(new CData("\\vspace{0.5pc}"));
        boolean poOrDeptNo = false;
        if (!po.isEmpty()) {
            poOrDeptNo = true;
            elements.add(new CData("P/O: " + po + "\\\\"));
        }

        if (!deptNo.isEmpty()) {
            poOrDeptNo = true;
            elements.add(new CData("Dept \\# " + deptNo + "\\\\"));
        }

        if (poOrDeptNo)
            elements.add(new CData("\\vspace{5pc}"));

        elements.add(
            new CData("Box \\hspace{4pc} " + boxNo + "\\hspace{4pc} OF \\hspace{4pc} " + maxBox)
        );
        return elements.build();
    }

    /**
     * Runs the latex to pdf converter using my custom flags.
     *
     * @param texFileName The name of the tex file being converted to a pdf.
     * @return The process to see if it failed or not.
     */
    public static Process runPdfLatex(Settings settings, final String texFileName) {
        try {
            if (settings.pdfLatexPath.isEmpty())
                throw new RuntimeException("Latex path is empty.");

            return Runtime.getRuntime().exec(
                settings.pdfLatexPath + " -halt-on-error -quiet " + texFileName
            );
        } catch(IOException ex) {
            System.err.println("pdflatex failed: " + ex);
        }
        return null;
    }
}
