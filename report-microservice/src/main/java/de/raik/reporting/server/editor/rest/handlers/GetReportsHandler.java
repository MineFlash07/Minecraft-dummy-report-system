package de.raik.reporting.server.editor.rest.handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import de.raik.reporting.server.report.Report;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashSet;

/**
 * This handler handles the request to get
 * all reports from /reports/
 * it will use GET requests only
 *
 * @author Raik
 * @version 1.0
 */
public class GetReportsHandler extends ReportHttpHandler {

    /**
     * The GSON to print the json later
     */
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    /**
     * Constructor to set the regex pattern
     * with a string
     *
     * @param reports The report hash set to access
     */
    public GetReportsHandler(HashSet<Report> reports) {
        super("^\\/[rR][eE][pP][oO][rR][tT][sS]?(\\/?)$", reports);
    }

    /**
     * Handle method to return the list of reports
     * as json will check for get request
     *
     * @param httpExchange The http exchange to modify the http request
     */
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        //Checking for get request only and sending Method not allowed
        if (!httpExchange.getRequestMethod().equalsIgnoreCase("GET")) {
            httpExchange.sendResponseHeaders(405, 0);
            httpExchange.close();
            return;
        }

        //Creating json array to return
        JsonArray array = new JsonArray();
        this.getReports().forEach(report -> {
            JsonObject reportObject = new JsonObject();
            //Adding properties
            reportObject.addProperty("uuid", report.reportedUser().toString());
            reportObject.addProperty("reason", report.reportReason());
            reportObject.addProperty("reporter", report.reporter().toString());
            reportObject.addProperty("date", report.timestamp().toString());
        });

        //Sending json
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(httpExchange.getResponseBody()))) {
            writer.write(GSON.toJson(array));
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        //Setting content type and codes
        httpExchange.getResponseHeaders().add("Content-Type", "application/json");
        httpExchange.sendResponseHeaders(200, 0);
        httpExchange.close();
    }

}
