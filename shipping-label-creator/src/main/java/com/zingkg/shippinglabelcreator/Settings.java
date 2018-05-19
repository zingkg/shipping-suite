package com.zingkg.shippinglabelcreator;

import java.io.File;
import java.io.IOException;

import org.ini4j.Ini;

/**
 * This class is filled with static methods that will setup the ini file to be used when creating
 * and changing settings.
 */
public class Settings {
    public final String homeName;
    public final String homeAddress;
    public final String homeCity;
    public final String homeState;
    public final String homeZipCode;
    public final String pdfLatexPath;
    public final boolean shouldAddEntryLine;

    public Settings(
        String homeName,
        String homeAddress,
        String homeCity,
        String homeState,
        String homeZipCode,
        String pdfLatexPath,
        boolean shouldAddEntryLine
    ) {
        this.homeName = homeName;
        this.homeAddress = homeAddress;
        this.homeCity = homeCity;
        this.homeState = homeState;
        this.homeZipCode = homeZipCode;
        this.pdfLatexPath = pdfLatexPath;
        this.shouldAddEntryLine = shouldAddEntryLine;
    }

    /**
     * Reads all of the ini files settings.
     *
     * @param settingsFile The file which contains this program's settings.
     */
    public static Settings readINISettings(File settingsFile) {
        try {
            final Ini ini = new Ini(settingsFile);
            final Ini.Section settings = ini.get("Source");
            final String homeName = settings.get("name", "");
            final String homeAddress = settings.get("address", "");
            final String homeCity = settings.get("city", "");
            final String homeState = settings.get("state", "");
            final String homeZipCode = settings.get("zip_code", "");

            Ini.Section latex = ini.get("Latex");
            final String pdfLatexPath = latex.get("pdf_latex_path", "");
            final boolean shouldAddEntryLine = latex.get(
                "should_add_entry_line",
                "false"
            ).toLowerCase().equals("true");
            return new Settings(
                homeName,
                homeAddress,
                homeCity,
                homeState,
                homeZipCode,
                pdfLatexPath,
                shouldAddEntryLine
            );
        } catch(IOException ex) {
            throw new RuntimeException("Exception while read ini file: " + ex);
        }
    }
}
