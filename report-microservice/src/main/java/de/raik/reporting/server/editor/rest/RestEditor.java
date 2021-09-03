package de.raik.reporting.server.editor.rest;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import de.raik.reporting.server.editor.ReportEditor;
import de.raik.reporting.server.editor.rest.handlers.EditReportsHandler;
import de.raik.reporting.server.editor.rest.handlers.GetReportsHandler;
import de.raik.reporting.server.editor.rest.handlers.ReportHttpHandler;
import de.raik.reporting.server.report.Report;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.Executors;

/**
 * A rest
 */
public class RestEditor extends ReportEditor {

    /**
     * The http server used for the rest client
     * there will be one main handler in here
     * to parse everything manually
     */
    private HttpServer httpServer;

    /**
     * The collection of the reports to
     * access them
     */
    private HashSet<Report> reports;

    /**
     * The port of the web rest server
     * to go online on
     */
    private int port;

    /**
     * The auth key of the server needed
     * to use the end points
     */
    private String authKey;

    /**
     * List of report handlers to add handlers for the functionality
     */
    private final HashSet<ReportHttpHandler> reportHandlers = new HashSet<>();

    /**
     * Method sending the reports hashset
     * to let the accessor access the reports
     *
     * @param reports The collections of the reports which will be sent to the accessor
     */
    @Override
    public void initAccessor(HashSet<Report> reports) {
        this.reports = reports;

        //Add handlers
        this.reportHandlers.add(new GetReportsHandler(this.reports));
        this.reportHandlers.add(new EditReportsHandler(this.reports, this.getUpdateCallback()));

        //Setting up http server
        try {
            this.httpServer = HttpServer.create(new InetSocketAddress(this.port), 0);
            //Creating one context handler to do uri handling manually
            this.httpServer.createContext("/", this::handle);
            //Setting 10 threads pool should be enough for this little microservice
            this.httpServer.setExecutor(Executors.newFixedThreadPool(10));

            this.httpServer.start();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Getter for the prefix for arguments in the command line
     * to parse arguments to the accessor argument: prefix.argument=value
     * Will be used by the argument parser
     *
     * @return The prefix for the arguments passed
     */
    @Override
    public String getConfigArgumentPrefix() {
        return "rest";
    }

    /**
     * Method to process the arguments loaded from the cli and having the set prefix
     * Will be called by the argument parser
     *
     * @param arguments Map of arguments with key as the attribute and value as its value
     */
    @Override
    public void loadArguments(HashMap<String, String> arguments) {
        this.port = Integer.parseInt(arguments.get("port"));
        this.authKey = arguments.get("authKey");
    }

    /**
     * Method called by the report server when the whole thing gets closed to
     * disable the editor service if needed
     */
    @Override
    public void shutdown() {
        this.httpServer.stop(1);
    }

    /**
     * Handle http request to use the right urls
     * and handle the right requests
     * It will get called by the http server
     *
     * @param httpExchange The http exchange state to access the http values
     */
    public void handle(HttpExchange httpExchange) throws IOException {
        //Check for Authentication
        if (!this.authKey.isEmpty()) {
            // Send Unauthorized if no Authorization is available
            if (!httpExchange.getRequestHeaders().containsKey("Authorization")) {
                httpExchange.sendResponseHeaders(401, 0);
                httpExchange.close();
                return;
            }

            //Check for the right token and send Permission denied if token is invalid
            if (!httpExchange.getRequestHeaders().getFirst("Authorization").equals("Basic " + this.authKey)) {
                httpExchange.sendResponseHeaders(403, 0);
                httpExchange.close();
                return;
            }
        }

        this.reportHandlers.stream().filter(handler -> handler.matches(httpExchange.getRequestURI().toString()))
                .findAny().ifPresentOrElse(handler -> {
                    try {
                        handler.handle(httpExchange);
                    } catch (IOException exception) {
                        exception.printStackTrace();
                    }
                }, () -> {
                    //Sending 404 if not found
                    try {
                        httpExchange.sendResponseHeaders(404, 0);
                    } catch (IOException exception) {
                        exception.printStackTrace();
                    }
                    httpExchange.close();
                });
    }
}
