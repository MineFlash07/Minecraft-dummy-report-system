package de.raik.reporting.spigot.command;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import de.raik.reporting.spigot.ReportPlugin;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Sub command showing the list of all reports
 * which were made
 *
 * @author Raik
 * @version 1.0
 */
public class ListSubCommand implements TabExecutor {

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
    public ListSubCommand(ReportPlugin plugin) {
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
        //Make request
        HttpRequest request = this.plugin.getPreDefinedHttpBuilder("reports").GET().build();
        //Setup client and make request
        HttpResponse<String> response;
        try {
            response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        } catch (IOException | InterruptedException exception) {
            exception.printStackTrace();
            sender.sendMessage("§cAn error has occurred.");
            return true;
        }
        //Check for response code
        if (response.statusCode() != 200) {
            sender.sendMessage("§cAn error has occurred.");
            return true;
        }

        //Parse json
        JsonArray reports;
        try {
            reports = (JsonArray) new JsonParser().parse(response.body());
        } catch (JsonParseException | ClassCastException exception) {
            sender.sendMessage("§cAn error has occurred.");
            return true;
        }

        this.sendList(reports, sender);
        //Always return true
        return true;
    }

    /**
     * Method sending the reports
     * to the player
     *
     * @param reportArray The json array containing the reports
     * @param sender The sender to send messages to
     */
    private void sendList(JsonArray reportArray, CommandSender sender) {
        //Put reports in list
        ArrayList<JsonObject> reportList = new ArrayList<>();
        reportArray.forEach(element -> {
            if (element instanceof JsonObject jsonObject) {
                reportList.add(jsonObject);
            }
        });
        //Sort by date and reverse to start from beginning
        reportList.sort(Comparator.comparing((JsonObject firstReport) -> LocalDateTime.parse(firstReport.get("date").getAsString())));
        Collections.reverse(reportList);

        //Print header
        sender.sendMessage("§ePlayer §7| §eReason §7| §eReporter | §eTimestamp");
        //Print all reports
        reportList.forEach(jsonObject -> sender.sendMessage(String.format("§f%s §7| §f%s §7| §f%s | §f%s",
                Bukkit.getOfflinePlayer(UUID.fromString(jsonObject.get("uuid").getAsString())).getName(),
                jsonObject.get("reason").getAsString(),
                Bukkit.getOfflinePlayer(UUID.fromString(jsonObject.get("reporter").getAsString())).getName(),
                jsonObject.get("date").getAsString())));

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
        //Always return empty list as this is one argument only
        return Collections.emptyList();
    }
}
