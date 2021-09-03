package de.raik.reporting.spigot.command;

import com.google.gson.JsonObject;
import de.raik.reporting.spigot.ReportPlugin;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

/**
 * The report command having the basic report functionality
 * and sub commands
 */
public class ReportCommand implements TabExecutor {

    /**
     * Plugin instance to create web requests later
     */
    private final ReportPlugin plugin;

    /**
     * Collection of sub commands to scan for later
     * The key is the subcommand name. The subcommands can only be triggered in console
     * or with permission
     */
    private final HashMap<String, TabExecutor> subCommands = new HashMap<>();

    /**
     * Constructor setting plugin for the command
     * and registering sub commands
     *
     * @param plugin The plugin instance
     */
    public ReportCommand(ReportPlugin plugin) {
        this.plugin = plugin;
        //Register sub commands
        this.subCommands.put("list", new ListSubCommand(plugin));
        this.subCommands.put("delete", new DeleteSubCommand(plugin));
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
        // Check for too less arguments
        if (args.length < 1) {
            return false;
        }

        //Check for advanced command ussage and use them instead of normal command to get ahead
        if (sender instanceof ConsoleCommandSender || sender.hasPermission("reports.manage")) {
            TabExecutor executor = this.subCommands.get(args[0].toLowerCase());
            if (executor != null) {
                return executor.onCommand(sender, command, label, args);
            }
        }

        //Check for non player sender
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cPlayer only command!");
            return true;
        }

        //Check for reason arg
        if (args.length < 2) {
            return false;
        }

        //Get online player to report and check if available
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            return false;
        }
        //Creating reason from all other args
        String reason = String.join(" ", Arrays.copyOfRange(args, 1, args.length));

        this.makeRequest(target, reason, (Player) sender);
        return true;
    }

    /**
     * Method to make the request with http and
     * json stuff
     *
     * @param target The target player to report
     * @param reason The reason
     * @param reporter The person who reported the player
     */
    private void makeRequest(Player target, String reason, Player reporter) {
        //Creating the request object to set properties
        JsonObject requestObject = new JsonObject();
        requestObject.addProperty("reason", reason);
        requestObject.addProperty("reporter", reporter.getUniqueId().toString());

        //Setting up http
        HttpRequest request = this.plugin.getPreDefinedHttpBuilder("reports/" + target.getUniqueId())
                .POST(HttpRequest.BodyPublishers.ofString(requestObject.toString()))
                .build();

        //Sending thr request
        HttpResponse<Void> response;
        try {
            response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.discarding());
        } catch (IOException | InterruptedException exception) {
            exception.printStackTrace();
            reporter.sendMessage("§cAn error has occurred.");
            return;
        }

        reporter.sendMessage(response.statusCode() == 204 ? "Reported successfully": "An error occured");
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
        //get advance to decide for subcommands
        boolean advanced = sender instanceof ConsoleCommandSender || sender.hasPermission("reports.manage");
        //Check args smaller one and returning the sub commands ornull
        if (args.length < 1) {
            return advanced ? new ArrayList<>(this.subCommands.keySet()) : null;
        }

        if (!advanced) {
            //Returning empty list as the reason needs to be filled by the user
            return Collections.emptyList();
        }

        //Search for advanced commands in the first argument and use the completion of the user lists in this case
        TabExecutor executor = this.subCommands.get(args[0].toLowerCase());
        if (executor != null) {
            return executor.onTabComplete(sender, command, alias, args);
        }

        return Collections.emptyList();
    }
}
