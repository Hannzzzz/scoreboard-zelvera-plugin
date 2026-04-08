package com.zelvera.scoreboard;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;

public class LeaderboardCommand implements CommandExecutor {

    private final ScoreboardPlugin plugin;

    public LeaderboardCommand(ScoreboardPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("§eUsage: /leaderboard <type> [limit]");
            sender.sendMessage("§eTypes: kills, deaths, wins, losses, games, kd");
            sender.sendMessage("§eExample: /leaderboard kills 10");
            return true;
        }

        LeaderboardManager.LeaderboardType type;
        try {
            type = parseType(args[0]);
        } catch (IllegalArgumentException e) {
            sender.sendMessage("§cInvalid leaderboard type! Use: kills, deaths, wins, losses, games, kd");
            return true;
        }

        int limit = 10; // Default
        if (args.length > 1) {
            try {
                limit = Integer.parseInt(args[1]);
                if (limit < 1 || limit > 50) {
                    limit = 10;
                }
            } catch (NumberFormatException e) {
                sender.sendMessage("§cInvalid limit! Using default of 10.");
            }
        }

        List<Map.Entry<String, Object>> leaderboard = plugin.getLeaderboardManager().getLeaderboard(type, limit);

        if (leaderboard.isEmpty()) {
            sender.sendMessage("§cNo leaderboard data available!");
            return true;
        }

        sender.sendMessage("§6§l" + type.getDisplayName() + " Leaderboard");
        sender.sendMessage("§7§m" + "=".repeat(30));

        int rank = 1;
        for (Map.Entry<String, Object> entry : leaderboard) {
            String line = plugin.getLeaderboardManager().formatLeaderboardEntry(type, rank, entry.getKey(), entry.getValue());
            sender.sendMessage(line);
            rank++;
        }

        return true;
    }

    private LeaderboardManager.LeaderboardType parseType(String arg) {
        switch (arg.toLowerCase()) {
            case "kills": return LeaderboardManager.LeaderboardType.KILLS;
            case "deaths": return LeaderboardManager.LeaderboardType.DEATHS;
            case "wins": return LeaderboardManager.LeaderboardType.WINS;
            case "losses": return LeaderboardManager.LeaderboardType.LOSSES;
            case "games": return LeaderboardManager.LeaderboardType.GAMES_PLAYED;
            case "kd": return LeaderboardManager.LeaderboardType.KD_RATIO;
            default: throw new IllegalArgumentException("Invalid type");
        }
    }
}