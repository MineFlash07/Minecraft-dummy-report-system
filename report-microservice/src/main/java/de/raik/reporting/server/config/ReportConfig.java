package de.raik.reporting.server.config;

import de.raik.reporting.server.accessor.oneussage.OneUsageAccessor;

/**
 * Interface representing a config which stores the reports
 * in a storing option like a file or database
 * The report server will load one main config from the arguments
 * and using the service loader:
 * Adding service with name: de.raik.reporting.server.config.ReportConfig
 *
 * @author Raik
 * @version 1.0
 */
public interface ReportConfig extends OneUsageAccessor {

    /**
     * Method to save the config when something has changed or
     * at the end depending on the saving mode
     */
    void saveConfig();

    /**
     * Method returning whether the config should be saved any time
     * the reports update or not. This influences the behavior how often
     * saveConfig will be called by the Report server
     *
     * @return whether it should save everytime or not
     */
    boolean shouldSaveEverytime();

}
