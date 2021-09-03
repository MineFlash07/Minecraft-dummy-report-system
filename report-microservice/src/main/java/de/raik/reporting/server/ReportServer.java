package de.raik.reporting.server;

import de.raik.reporting.server.accessor.ReportAccessor;
import de.raik.reporting.server.accessor.ReportAccessorService;
import de.raik.reporting.server.config.ConfigFactory;
import de.raik.reporting.server.config.ReportConfig;
import de.raik.reporting.server.editor.EditorFactory;
import de.raik.reporting.server.editor.ReportEditor;
import de.raik.reporting.server.launch.LaunchArgumentParser;
import de.raik.reporting.server.report.Report;

import java.util.HashSet;
import java.util.Scanner;

/**
 * The report server controlling the reports
 * managing the config and the editor. A csv and
 * rest config by default
 * it will also store the reports
 *
 * @author Raik
 * @version 1.0
 */
public final class ReportServer {

    /**
     * The collection of the reports to store them
     */
    private final HashSet<Report> reports = new HashSet<>();

    /**
     * The main configuration of the plugin which should manage saving and loading the data
     */
    private ReportConfig config;

    /**
     * The main editor of the plugin which should take care of editing the data
     */
    private ReportEditor editor;

    /**
     * Creating the report server
     * with setting up config and editor and
     * all report accessors
     *
     * @param arguments The arguments parsed from the command line
     */
    public ReportServer(String[] arguments) {
        //Creating parser and setting the configClass and the editor class
        LaunchArgumentParser parser = new LaunchArgumentParser(arguments)
                .supplyArgument("configClass", configClass -> this.config = new ConfigFactory().create(configClass))
                .supplyArgument("editorClass", editorClass -> this.editor = new EditorFactory().create(editorClass));

        //Check for none config and editor
        if (this.config == null || this.editor == null) {
            throw new IllegalArgumentException("Either the editor or config class are invalid");
        }

        //Supply to config and editor
        parser.supplyToOneUsageAccessor(this.config)
                .supplyToOneUsageAccessor(this.editor);

        /*
         * The accessor having access to the reports to edit them dynamically
         * Create and set accessors. Also adding config and editor
         * Using ReportAccessorService to get all registered accessors
         */
        HashSet<ReportAccessor> accessors = new HashSet<>(new ReportAccessorService<>(ReportAccessor.class).getLoaded());
        accessors.add(this.config);
        accessors.add(this.editor);

        // Initialize accessors
        accessors.forEach(accessor -> accessor.initAccessor(this.reports));

        //Finished - Wait for close
        this.waitForCloseCommand();
    }

    /**
     * After setting up wait for the close
     * with scanner checking the console input
     *
     * It will shutdown the editor later
     */
    private void waitForCloseCommand() {
        System.out.println("Report-Server started.");

        Scanner scanner = new Scanner(System.in);
        boolean notClosed;
        do {
            System.out.print("\nYou need to type \"close\" to close.\n> ");
            notClosed = scanner.nextLine().equalsIgnoreCase("close");
        } while (!notClosed);

        //Close the editor
        this.editor.shutdown();
    }

}
