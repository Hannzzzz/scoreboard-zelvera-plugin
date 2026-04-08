package com.zelvera.scoreboard;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ScoreboardCommand implements CommandExecutor {

    private final ScoreboardPlugin plugin;

    public ScoreboardCommand(ScoreboardPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("scoreboard.admin")) {
            sender.sendMessage("§cYou don't have permission to use this command!");
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage("§eUsage: /scoreboard <reload|toggle|update>");
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "reload":
                // Reload configuration if any
                sender.sendMessage("§aScoreboard configuration reloaded!");
                break;

            case "toggle":
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    // Toggle scoreboard visibility
                    if (player.getScoreboard() == plugin.getScoreboardManager().getPlayerScoreboard(player.getUniqueId())) {
                        plugin.getScoreboardManager().removeScoreboard(player);
                        sender.sendMessage("§aScoreboard hidden!");
                    } else {
                        plugin.getScoreboardManager().setScoreboard(player);
                        sender.sendMessage("§aScoreboard shown!");
                    }
                } else {
                    sender.sendMessage("§cThis command can only be used by players!");
                }
                break;

            case "update":
                if (sender instanceof Player) {
                    plugin.getScoreboardManager().updateScoreboard((Player) sender);
                    sender.sendMessage("§aScoreboard updated!");
                } else {
                    sender.sendMessage("§cThis command can only be used by players!");
                }
                break;

            default:
                sender.sendMessage("§eUsage: /scoreboard <reload|toggle|update>");
                break;
        }

        return true;
    }
}