package de.raik.reporting.server.config.file;

import de.raik.reporting.server.report.Report;
import de.raik.reporting.server.report.ReportFactory;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashSet;

/**
 * A file config to get the reports
 * from a .csv file to store the files
 * in
 * It's registered in the services files
 *
 * @author Raik
 * @version 1.0
 */
public class CSVConfig extends FileConfig {

    /**
     * The header line of the csv file to set the columns
     */
    private final String headerLine;

    /**
     * The reports set stored to access it to save
     */
    private HashSet<Report> reports;

    /**
     * Constructor to create the header line
     * of the fields of the report records to use them
     * as columns
     */
    public CSVConfig() {
        Field[] reportFields = Report.class.getDeclaredFields();
        String[] fieldNames = new String[reportFields.length];

        for (int i = 0; i < reportFields.length; i++) {
            fieldNames[i] = reportFields[i].getName();
        }

        this.headerLine = String.join(",", fieldNames);
    }

    /**
     * Method sending the reports hashset
     * to let the accessor access the reports
     *
     * @param reports The collections of the reports which will be sent to the accessor
     */
    @Override
    public void initAccessor(HashSet<Report> reports) {
        this.reports = reports;
        ReportFactory factory = new ReportFactory();
        this.setup(reader -> {
            try {
                //Read one line because it's the header line
                reader.readLine();
                //Use for loop to read lines
                for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                    reports.add(factory.createFromStringData(line.split(",")));
                }
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        });
    }

    /**
     * Method to save the config when something has changed or
     * at the end depending on the saving mode
     */
    @Override
    public void saveConfig() {
        this.write(writer -> {
            try {
                //Write header line
                writer.write(this.headerLine);
                // Add reports as line
                for (Report report : this.reports) {
                    writer.append("\n").append(String.join(",", report.reportedUser().toString(), report.reportReason(),
                            report.reporter().toString(), report.timestamp().toString()));
                }
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        });
    }

    /**
     * Getter for the file ending to get the file ending
     * of the file config later
     *
     * @return The file ending e.g .json
     */
    @Override
    public String getFileEnding() {
        return ".csv";
    }
}
