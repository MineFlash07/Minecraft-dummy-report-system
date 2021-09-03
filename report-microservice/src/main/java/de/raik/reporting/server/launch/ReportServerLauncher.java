package de.raik.reporting.server.launch;

import de.raik.reporting.server.ReportServer;

/**
 * Class holding the main method
 * to start the server with executing the
 * archive file
 *
 * @author Raik
 * @version 1.0
 */
public class ReportServerLauncher {

    /**
     * The main method creating a new report server
     * which will start the whole service. It will parse the arguments
     * into the server to set the important properties
     *
     * @param args The start arguments from the command line and executor
     *             to parse into the server to let the server get needed
     *             information
     */
    public static void main(String[] args) {
        //Creating a new instance to create the server
        new ReportServer(args);
    }

}
