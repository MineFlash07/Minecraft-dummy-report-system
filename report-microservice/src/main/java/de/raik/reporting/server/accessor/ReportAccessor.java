package de.raik.reporting.server.accessor;

import de.raik.reporting.server.report.Report;

import java.util.HashSet;

/**
 * A report accessor can access the reports
 * stored in the report server as the server will scan
 * for accessor and use them
 * Adding service with name: de.raik.reporting.server.accessor.ReportAccessor
 *
 * @author Raik
 * @version 1.0
 */
public interface ReportAccessor {

    /**
     * Method sending the reports hashset
     * to let the accessor access the reports
     *
     * @param reports The collections of the reports which will be sent to the accessor
     */
    void initAccessor(HashSet<Report> reports);

}
