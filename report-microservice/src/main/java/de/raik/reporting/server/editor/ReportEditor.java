package de.raik.reporting.server.editor;

import de.raik.reporting.server.accessor.oneussage.OneUsageAccessor;

/**
 * Abstract class represent the editors which should
 * add and modify the reports. An example is a REST-Api
 * The report server will only load one report editor from the services
 * by using the service loader
 * Adding service with name: de.raik.reporting.server.editor.ReportEditor
 *
 * @author Raik
 * @version 1.0
 */
public abstract class ReportEditor implements OneUsageAccessor {

    /**
     * A callback runnable which should be called
     * when the reports get updated it also has a setter and getter
     */
    private Runnable updateCallback = null;

    public void setUpdateCallback(Runnable updateCallback) {
        this.updateCallback = updateCallback;
    }

    protected Runnable getUpdateCallback() {
        return this.updateCallback;
    }

    /**
     * Method called by the report server when the whole thing gets closed to
     * disable the editor service if needed
     */
    public abstract void shutdown();

}
