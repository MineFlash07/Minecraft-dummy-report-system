package de.raik.reporting.server.editor.rest.handlers;

import com.sun.net.httpserver.HttpHandler;
import de.raik.reporting.server.report.Report;

import java.util.HashSet;
import java.util.regex.Pattern;

/**
 * Specific http handler to use for the reports
 * it's the parent class for the http handlers
 * it handles every /reports endpoint
 *
 * @author Raik
 * @version 1.0
 */
public abstract class ReportHttpHandler implements HttpHandler {

    /**
     * The regex pattern to test for uris
     * it will indicate which url will be triggered
     * by the handler
     * it will be set in the constructor
     */
    private final Pattern pattern;

    /**
     * The hash set storing reports
     * to access them
     */
    private final HashSet<Report> reports;

    /**
     * Constructor to set the regex pattern
     * with a string
     *
     * @param regexString The regex pattern as a string
     * @param reports The report hash set to access
     */
    public ReportHttpHandler(String regexString, HashSet<Report> reports) {
        this.reports = reports;
        this.pattern = Pattern.compile(regexString);
    }

    protected HashSet<Report> getReports() {
        return this.reports;
    }

    /**
     * Method indicating whether the requested uri matches the string to
     * to handle the request as wanted
     *
     * @param uri The uri to check
     * @return Whether the uri matches
     */
    public boolean matches(String uri) {
        return this.pattern.matcher(uri).matches();
    }

}
