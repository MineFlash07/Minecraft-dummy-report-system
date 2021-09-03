package de.raik.reporting.server.config.file;

import de.raik.reporting.server.config.ReportConfig;

import java.io.*;
import java.util.HashMap;
import java.util.function.Consumer;

/**
 * Abstract file config to set a default behavior for files
 * as a config. THis could be needed if someone wants to store
 * the reports in a file
 *
 * @author Raik
 * @version 1.0
 */
public abstract class FileConfig implements ReportConfig {

    /**
     * The file of the file config
     * to access, read and edit
     */
    private File file;

    /**
     * Getter for the prefix for arguments in the command line
     * to parse arguments to the accessor argument: prefix.argument=value
     * Will be used by the argument parser
     *
     * @return The prefix for the arguments passed
     */
    @Override
    public String getConfigArgumentPrefix() {
        //Basic prefix for the configurations of the file
        return "file";
    }

    /**
     * Setting up the folder and file and read the file
     * if exists using the reader consumer to use the reader
     *
     * @param readerConsumer The reader consumer which uses a buffered
     *                       reader to check the file content
     */
    public void setup(Consumer<BufferedReader> readerConsumer) {
        //Check directory
        this.file.getParentFile().mkdirs();

        //Check if file exists
        if (!this.file.exists()) {
            return;
        }

        //Creating the auto closable reader in try catch and calling the consumer
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(this.file)))) {
            readerConsumer.accept(reader);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Writing to the file of file config
     * using consumer again to use whole writer
     *
     * @param writerConsumer A write consumer to access the writer
     *                       from outer to write to the file
     */
    public void write(Consumer<BufferedWriter> writerConsumer) {
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(this.file)))) {
            writerConsumer.accept(writer);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Method to process the arguments loaded from the cli and having the set prefix
     * Will be called by the argument parser
     *
     * @param arguments Map of arguments with key as the attribute and value as its value
     */
    @Override
    public void loadArguments(HashMap<String, String> arguments) {
        //Loading the arguments to get everything and setup the files
        String fileName = arguments.get("name");
        this.file = new File(arguments.get("path"), fileName.endsWith(this.getFileEnding()) ? fileName : fileName + this.getFileEnding());
    }

    /**
     * Method returning whether the config should be saved any time
     * the reports update or not. This influences the behavior how often
     * saveConfig will be called by the Report server
     *
     * @return whether it should save everytime or not
     */
    @Override
    public boolean shouldSaveEverytime() {
        // Using false because changing a file every time is lazy
        return false;
    }

    /**
     * Getter for the file ending to get the file ending
     * of the file config later
     *
     * @return The file ending e.g .json
     */
    public abstract String getFileEnding();

}
