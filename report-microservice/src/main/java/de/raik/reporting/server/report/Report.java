package de.raik.reporting.server.report;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * A record representing a report for all needed information
 * to store reports in one object
 *
 * Attributes:
 * reportedUser - The user who was reported. Saved as uuid to be independent of name changes
 * reportReason - The reason why the player has been reported
 * reporter - The person who reported the reported user to retrace the report later. Saved as uuid for same reason as reportedUser
 * timestamp - The timestamp when the report was made to retrace it
 *
 * @author Raik
 * @version 1.0
 */
public record Report(UUID reportedUser, String reportReason, UUID reporter, LocalDateTime timestamp) {
}
