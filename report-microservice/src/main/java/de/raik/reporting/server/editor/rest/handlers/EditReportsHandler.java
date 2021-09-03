package de.raik.reporting.server.editor.rest.handlers;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import de.raik.reporting.server.report.Report;
import de.raik.reporting.server.report.ReportFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;

public class EditReportsHandler extends ReportHttpHandler {

    /**
     * The callback which should be called when
     * something has updated
     */
    private final Runnable updateCallback;

    /**
     * The report factory to create reports
     */
    private final ReportFactory factory = new ReportFactory();

    /**
     * Constructor to set the regex pattern
     * with a string
     *
     * @param reports The report hash set to access
     * @param updateCallback The update callback to update
     */
    public EditReportsHandler(HashSet<Report> reports, Runnable updateCallback) {
        super("^\\/[rR][eE][pP][oO][rR][tT][sS]\\/[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}?(\\/?)$", reports);
        this.updateCallback = updateCallback;
    }

    /**
     * Handle method to edit the list with adding or deleting
     * using POST or DELETE requests
     *
     * @param httpExchange The http exchange to modify the http request
     */
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        String reportedUUID = httpExchange.getRequestURI().toString();
        reportedUUID = reportedUUID.endsWith("/")
                ? reportedUUID.substring(reportedUUID.indexOf("/", 2) + 1, reportedUUID.length() - 1)
                : reportedUUID.substring(reportedUUID.indexOf("/", 2) + 1);

        switch (httpExchange.getRequestMethod().toUpperCase()) {
            case "POST" -> {
                JsonObject reportObject;

                //Read json from request and send bad request if required
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(httpExchange.getRequestBody()))) {
                    reportObject = (JsonObject) JsonParser.parseReader(reader);
                } catch (ClassCastException | JsonParseException exception) {
                    this.sendBadRequest(httpExchange);
                    return;
                }

                //Check keys json
                if (!reportObject.has("reason") || !reportObject.has("reporter")) {
                    this.sendBadRequest(httpExchange);
                    return;
                }

                //Create new report and send bad request if the uuid is wrong somehow
                try {
                    this.getReports().add(this.factory.createNewFromSpecified(reportedUUID, reportObject.get("reason").getAsString(),
                            reportObject.get("reporter").getAsString()));
                } catch (IllegalArgumentException exception) {
                    this.sendBadRequest(httpExchange);
                    return;
                }
                //Run update callback
                this.updateCallback.run();
            }
            case "DELETE" -> {
                String finalReportedUUID = reportedUUID;
                this.getReports().removeIf(report -> report.reportedUser().toString().equalsIgnoreCase(finalReportedUUID));
                this.updateCallback.run();
            }
            default -> {
                //Send method not allowed on default
                httpExchange.sendResponseHeaders(405, 0);
                httpExchange.close();
                return;
            }
        }

        //Send request with no content
        httpExchange.sendResponseHeaders(204, 0);
        httpExchange.close();
    }

    /**
     * Send bad request when something has failed.
     * It will send 400 status code and close the exchane
     *
     * @param httpExchange The exchange to access
     */
    private void sendBadRequest(HttpExchange httpExchange) throws IOException {
        httpExchange.sendResponseHeaders(400, 0);
        httpExchange.close();
    }

}
