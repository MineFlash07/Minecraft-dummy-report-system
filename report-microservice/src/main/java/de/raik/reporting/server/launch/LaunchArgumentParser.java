package de.raik.reporting.server.launch;

import de.raik.reporting.server.accessor.oneussage.OneUsageAccessor;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Argument parser to parse the launcher arguments
 * into key and value in the format
 * java ... key=value key2=value2 prefix.key3=value3
 * to get the confiurations with the arguments
 * more easily
 *
 * @author Raik
 * @version 1.0
 */
public class LaunchArgumentParser {

    /**
     * A default map of prefix.key and its value to define default values
     * to start the service even without parsing specific or even none
     * arguments.
     */
    private static final Map<String, String> DEFAULT_ARGUMENTS = Map.of(
            "file.name", "reports.csv",
            "file.path", "./",
            "configClass", "de.raik.reporting.server.config.file.CSVConfig",
            "rest.port", "1337",
            "rest.authKey", "hKw0dKTBJ2KLqgzJVRAV2aJu",
            "editorClass", "de.raik.reporting.server.editor.rest.RestEditor"
    );

    /**
     * HashMap containing all parsed arguments
     * The key of the outer hashmap is the prefix of the setting.
     * Settings without prefix will have "", an empty string as outer key
     * The second map will have the key without its prefix and the final value
     */
    private final HashMap<String, HashMap<String, String>> parsedArguments = new HashMap<>();

    /**
     * Constructor taking the arguments and parsing them
     * completely will use both DEFAULT and given args
     *
     * @param args The arguments from the command line to parse
     */
    public LaunchArgumentParser(String[] args) {
        // Map with prefix.key as key and the value as value having the default arguments by default
        var prefixKeyValueMap = new HashMap<>(DEFAULT_ARGUMENTS);

        /*
         * Putting every prefix.key=value arguments into value map
         * This will overwrite the default arguments
         * Also filtering for = is in and not the last or first character to that no none key
         * or value arguments get put in
         * Also checking that the prefix.key dot separator is not at the beginning and not directly
         * before the =
         */
        Arrays.stream(args)
                .filter(argument -> argument.indexOf("=") > 0 && argument.indexOf("=") != argument.length() - 1
                        && (!argument.contains(".") || argument.indexOf(".") > argument.indexOf("=")
                        || (argument.indexOf(".") > 0 &&  argument.indexOf(".") != argument.indexOf("=") - 1)))
                .forEach(argument -> prefixKeyValueMap.put(argument.split("=", 2)[0], argument.split("=", 2)[1]));

        // Iterating through all values of the prefix key value map to fill the parsed
        prefixKeyValueMap.forEach((prefixKey, value) -> {
            String[] prefixKeySplit = prefixKey.split("\\.", 2);
            //Put in the right map from the key splits
            this.getKeyHashMap(prefixKeySplit).put(prefixKeySplit.length == 2 ? prefixKeySplit[1]: prefixKeySplit[0], value);
        });
    }

    /**
     * Functional method taking a whole key and a consumer to use the value
     * e.g. parser.supplyArgument("prefix.key", System.out::println);
     *
     * @param key The key of the target value like prefix.key
     * @param consumer The consumer taking the value to process it
     * @return Returning the parser to use functional
     */
    public LaunchArgumentParser supplyArgument(String key, Consumer<String> consumer) {
        String[] prefixKeySplit = key.split("\\.", 2);
        //Get the right map and get the value from the key which will be accepted into the consumer
        consumer.accept(this.getKeyHashMap(prefixKeySplit).get(prefixKeySplit.length == 2 ? prefixKeySplit[1]: prefixKeySplit[0]));

        return this;
    }

    /**
     * Functional method taking an OneUsageAccessor to setup its config in the accessor
     * using the argument prefix and the loadArguments
     *
     * @param accessor The accessor to supply its settings to
     * @return Returning the parser to use functional
     */
    public LaunchArgumentParser supplyToOneUsageAccessor(OneUsageAccessor accessor) {
        accessor.loadArguments(this.parsedArguments.get(accessor.getConfigArgumentPrefix()));
        return this;
    }

    /**
     * Get the hash map for the prefix key combo to set the
     * right key into the right map
     *
     * @param prefixKeySplit The array of the prefix and key split
     * @return The hashmap for the key and prefix
     */
    private HashMap<String, String> getKeyHashMap(String[] prefixKeySplit) {
        //Set key to "" if no prefix is there. Set it to the prefix if available
        String key = prefixKeySplit.length < 2 ? "" : prefixKeySplit[0];

        //Add hashmap if not added
        if (!this.parsedArguments.containsKey(key)) {
            this.parsedArguments.put(key, new HashMap<>());
        }

        return this.parsedArguments.get(key);
    }

}
