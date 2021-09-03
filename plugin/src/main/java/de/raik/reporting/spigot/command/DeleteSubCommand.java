package de.raik.reporting.spigot.command;

import de.raik.reporting.spigot.ReportPlugin;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

/**
 * Sub command to delete all reports of a whole user
 * it's permission bound
 *
 * @author Raik
 * @version 1.0
 */
public class DeleteSubCommand implements TabExecutor {

    /**
     * Plugin instance to create web requests later
     */
    private final ReportPlugin plugin;

    /**
     * Constructor setting plugin for the command
     * and registering sub commands
     *
     * @param plugin The plugin instance
     */
    public DeleteSubCommand(ReportPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Executes the given command, returning its success.
     * <br>
     * If false is returned, then the "usage" plugin.yml entry for this command
     * (if defined) will be sent to the player.
     *
     * @param sender  Source of the command
     * @param command Command which was executed
     * @param label   Alias of the command which was used
     * @param args    Passed command arguments
     * @return true if a valid command, otherwise false
     */
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        //check for arguments
        if (args.length < 2) {
            sender.sendMessage("§c/report delete <name>");
            return true;
        }

        //Get offline player
        OfflinePlayer target = Bukkit.getOfflinePlayerIfCached(args[1]);
        if (target == null) {
            sender.sendMessage("§c/report delete <name>");
            return true;
        }

        this.makeRequest(target, sender);

        return true;
    }

    /**
     * Method to make the request with http
     *
     * @param target The target player to report
     * @param issuer The person who executed the command
     */
    private void makeRequest(OfflinePlayer target, CommandSender issuer) {
        //Setting up http
        HttpRequest request = this.plugin.getPreDefinedHttpBuilder("reports/" + target.getUniqueId())
                .DELETE()
                .build();

        //Sending thr request
        HttpResponse<Void> response;
        try {
            response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.discarding());
        } catch (IOException | InterruptedException exception) {
            exception.printStackTrace();
            issuer.sendMessage("§cAn error has occurred.");
            return;
        }

        issuer.sendMessage(response.statusCode() == 204 ? "Reports cleared.": "An error occured");
    }

    /**
     * Requests a list of possible completions for a command argument.
     *
     * @param sender  Source of the command.  For players tab-completing a
     *                command inside of a command block, this will be the player, not
     *                the command block.
     * @param command Command which was executed
     * @param alias   The alias used
     * @param args    The arguments passed to the command, including final
     *                partial argument to be completed and command label
     * @return A List of possible completions for the final argument, or null
     * to default to the command executor
     */
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        //Always return null to let players as a player needs to be specified
        return null;
    }
}
