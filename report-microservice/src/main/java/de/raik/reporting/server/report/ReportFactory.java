package de.raik.reporting.server.report;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Factory to create report instances
 * with specific arguments because
 * reports can be created from both the Rest-Service and
 * the configuration
 *
 * @author Raik
 * @version 1.0
 */
public class ReportFactory {

    /**
     * Create a report with player and reason specific
     * attributes it's specified as new because the report timestamp
     * is set to the current timestamp of the creation
     * Should be used by REST and adding points
     *
     * @param reportedUser The reported User
     * @param reason The reason of the report
     * @param reporter The player who reported the user
     * @return The new created report
     */
    public Report createNewFromSpecified(String reportedUser, String reason, String reporter) {
        return new Report(UUID.fromString(reportedUser), reason, UUID.fromString(reporter), LocalDateTime.now());
    }

    /**
     * Create a report with a data string giving all attributes
     * of the report as string which will be parsed into the right
     * attribute objects. Should mainly be used by config setting
     * stuff
     *
     * @param dateString The data string containing all important information and all attributes. Only the first 4
     *                   arguments will be used. Others will be ignored
     *                   Zeroth index - The string uuid of the reported user
     *                   First index - The reason which is a string anyway
     *                   Second index - The string uuid of the user who reported the player
     *                   Third index - The string timestamp when the report was made which needs to passed to
     *                                  LocalDateTime
     * @throws IllegalArgumentException Throws exception when the data string is too short
     * @return The report created from the
     */
    public Report createFromStringData(String[] dateString) {
        //Check for the length and throw an IllegalArgumentException if too short
        if (dateString.length < 4) {
            throw new IllegalArgumentException("The data string needs 4 elements to create a report!");
        }

        return new Report(UUID.fromString(dateString[0]), dateString[1], UUID.fromString(dateString[2]),
                LocalDateTime.parse(dateString[3]));
    }

}
