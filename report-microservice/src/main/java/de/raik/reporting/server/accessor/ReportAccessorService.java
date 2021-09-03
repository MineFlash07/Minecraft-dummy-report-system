package de.raik.reporting.server.accessor;

import java.util.HashSet;
import java.util.ServiceLoader;

/**
 * Service using the ServiceLoader class to get
 * all registered accessor of the in the generics given
 * type to add functionality to the service without editing
 * the source and just adding some classes to the classh path
 *
 * @author Raik
 * @version 1.0
 * @param <T> The type the service will search classes of and return
 *           all found instances with the same type
 */
public class ReportAccessorService<T extends ReportAccessor> {

    /**
     * The service loader which will search for the services
     * registered with the same type as tit will return
     */
    private final ServiceLoader<T> serviceLoader;

    /**
     * Constructor to create the service loader
     * which will use the class instance needed
     * to load the service loader
     *
     * @param accessorClass The class of T needed by the loader
     *                      to load
     */
    public ReportAccessorService(Class<T> accessorClass) {
        this.serviceLoader = ServiceLoader.load(accessorClass);
    }

    /**
     * Method to get the loaded accessor which were
     * loaded by the service loader to use later
     *
     * @return A hashset containing the loaded accessor
     */
    public HashSet<T> getLoaded() {
        HashSet<T> loadedAccessor = new HashSet<>();
        // Looping through iterator to fill the set
        for (T loaded: this.serviceLoader) {
            loadedAccessor.add(loaded);
        }

        return loadedAccessor;
    }

}
