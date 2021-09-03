package de.raik.reporting.server.editor;

import de.raik.reporting.server.accessor.oneussage.OneUsageAccessorFactory;

/**
 * The factory for creating an editor using the OneUsageAccessorFactory
 * as super class
 *
 * @author Raik
 * @version 1.0
 */
public class EditorFactory extends OneUsageAccessorFactory<ReportEditor> {

    /**
     * Constructor setting the
     * class to the one usage accessor factory
     */
    public EditorFactory() {
        super(ReportEditor.class);
    }
}

