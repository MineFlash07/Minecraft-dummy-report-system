package de.raik.reporting.server.accessor.oneussage;

import de.raik.reporting.server.accessor.ReportAccessor;

import java.util.HashMap;

/**
 * Parent interface for interface accessor which will only
 * be used once especially reserved for Config and editors
 * of the reports
 *
 * Interface could be sealed and only permitting ReportConfig and ReportEditor in Java 17 or
 * in preview mode of Java 16
 *
 * @author Raik
 * @version 1.0
 */
public interface OneUsageAccessor extends ReportAccessor {

    /**
     * Getter for the prefix for arguments in the command line
     * to parse arguments to the accessor argument: prefix.argument=value
     * Will be used by the argument parser
     *
     * @return The prefix for the arguments passed
     */
    String getConfigArgumentPrefix();

    /**
     * Method to process the arguments loaded from the cli and having the set prefix
     * Will be called by the argument parser
     *
     * @param arguments Map of arguments with key as the attribute and value as its value
     */
    void loadArguments(HashMap<String, String> arguments);

}
