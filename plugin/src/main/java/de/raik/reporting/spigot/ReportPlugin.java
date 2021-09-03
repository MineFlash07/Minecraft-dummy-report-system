package de.raik.reporting.spigot;

import de.raik.reporting.spigot.command.ReportCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.net.URI;
import java.net.http.HttpRequest;
import java.util.Objects;

/**
 * The jave plugin needed by spigot to setup
 * everything for minecraft
 */
public class ReportPlugin extends JavaPlugin {

    /**
     * The start string of the uri to
     * set builder
     */
    private String startURIString;

    private String authToken = "Basic ";

    /**
     * On enable setting up the plugin
     */
    @Override
    public void onEnable() {
        this.saveDefaultConfig();

        //Setting start url asserting as it's required to be not null
        this.startURIString = this.getConfig().getString("microservice.urn");
        assert this.startURIString != null;
        this.startURIString = this.startURIString.endsWith("/") ? this.startURIString : this.startURIString + "/";
        //Setting auth token
        this.authToken += this.getConfig().getString("microservice.authKey");

        //Set up command
        ReportCommand reportCommand = new ReportCommand(this);
        Objects.requireNonNull(this.getCommand("report")).setExecutor(reportCommand);
        this.getCommand("report").setTabCompleter(reportCommand);
    }

    /**
     * Method returning already created builder
     * with uri and authorization to only set request specific
     * stuff in command
     *
     * @param urlPath The sub path of the url
     * @return The prepared builder
     */
    public HttpRequest.Builder getPreDefinedHttpBuilder(String urlPath) {
        return HttpRequest.newBuilder(URI.create(this.startURIString + urlPath))
                .header("Authorization", this.authToken);
    }

}
