package de.raik.reporting.server.accessor.oneussage;

import de.raik.reporting.server.accessor.ReportAccessorService;

import java.util.HashSet;

/**
 * Abstract factory for creating objects from the one usage
 * accessor it will use ReportAccessService to get all loaded things
 * and return the found objectz with the right name
 *
 * Class could be sealed and only permitting ReportConfig and ReportEditor in Java 17 or
 * in preview mode of Java 16
 *
 * @author Raik
 * @version 1.0
 */
public abstract class OneUsageAccessorFactory<T extends OneUsageAccessor> {

    /**
     * The loaded accessor from the ReportAccessorSerivce to scan and search
     * for the creation later
     */
    private final HashSet<T> loadedAccessor;

    /**
     * Constructor loading all registered one usage accessor
     * of the wanted class using a ReportAccessorService
     * to store so the factory can use them loter
     *
     * @param accessorClass The accessor class of the type to scan
     */
    public OneUsageAccessorFactory(Class<T> accessorClass) {
        this.loadedAccessor = new ReportAccessorService<>(accessorClass).getLoaded();
    }

    /**
     * Creates/Returns the accessor which is wanted. It searches through
     * the names and paths of the classes of the accessor to find the wanted one
     *
     * @param className The class name where the equivalent accessor it wanted
     * @return The wanted accessor or null if none has found
     */
    public T create(String className) {
        return this.loadedAccessor.stream()
                .filter(accessor -> accessor.getClass().getName().equals(className))
                .findFirst()
                .orElse(null);
    }

}
