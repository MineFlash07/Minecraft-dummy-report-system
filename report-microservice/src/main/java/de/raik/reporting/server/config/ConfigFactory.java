package de.raik.reporting.server.config;

import de.raik.reporting.server.accessor.oneussage.OneUsageAccessorFactory;

/**
 * The factory for creating a config using the OneUsageAccessorFactory
 * as super class
 *
 * @author Raik
 * @version 1.0
 */
public class ConfigFactory extends OneUsageAccessorFactory<ReportConfig> {

    /**
     * Constructor setting the
     * class to the one usage accessor factory
     */
    public ConfigFactory() {
        super(ReportConfig.class);
    }

}
