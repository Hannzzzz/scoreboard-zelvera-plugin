package com.zelvera.scoreboard;

import org.bukkit.Bukkit;

/**
 * API class for ConditionalCommands and other plugins to access leaderboard data
 * Usage in ConditionalCommands: %scoreboard_leaderboard_kills_1%
 * This would return the #1 player on the kills leaderboard
 */
public class LeaderboardAPI {

    private static ScoreboardPlugin plugin;

    public static void setPlugin(ScoreboardPlugin plugin) {
        LeaderboardAPI.plugin = plugin;
    }

    /**
     * Get a specific leaderboard position
     * @param type The leaderboard type (kills, deaths, wins, losses, games, kd)
     * @param position The position (1-based)
     * @return Formatted leaderboard line or empty string if not available
     */
    public static String getLeaderboardLine(String type, int position) {
        if (plugin == null || !Bukkit.getPluginManager().isPluginEnabled("BridgeFight")) {
            return "";
        }

        LeaderboardManager.LeaderboardType leaderboardType;
        try {
            leaderboardType = parseType(type);
        } catch (IllegalArgumentException e) {
            return "§cInvalid type: " + type;
        }

        return plugin.getLeaderboardManager().getLeaderboardLine(leaderboardType, position);
    }

    /**
     * Get leaderboard data for ConditionalCommands placeholder
     * Format: %scoreboard_leaderboard_[type]_[position]%
     */
    public static String getPlaceholderValue(String placeholder) {
        if (!placeholder.startsWith("scoreboard_leaderboard_")) {
            return null;
        }

        String[] parts = placeholder.substring("scoreboard_leaderboard_".length()).split("_");
        if (parts.length != 2) {
            return "";
        }

        String type = parts[0];
        int position;
        try {
            position = Integer.parseInt(parts[1]);
        } catch (NumberFormatException e) {
            return "";
        }

        return getLeaderboardLine(type, position);
    }

    private static LeaderboardManager.LeaderboardType parseType(String type) {
        switch (type.toLowerCase()) {
            case "kills": return LeaderboardManager.LeaderboardType.KILLS;
            case "deaths": return LeaderboardManager.LeaderboardType.DEATHS;
            case "wins": return LeaderboardManager.LeaderboardType.WINS;
            case "losses": return LeaderboardManager.LeaderboardType.LOSSES;
            case "games": return LeaderboardManager.LeaderboardType.GAMES_PLAYED;
            case "kd": return LeaderboardManager.LeaderboardType.KD_RATIO;
            default: throw new IllegalArgumentException("Invalid type: " + type);
        }
    }

    // Additional utility methods for ConditionalCommands
    public static String getKillsLeaderboard(int position) {
        return getLeaderboardLine("kills", position);
    }

    public static String getDeathsLeaderboard(int position) {
        return getLeaderboardLine("deaths", position);
    }

    public static String getWinsLeaderboard(int position) {
        return getLeaderboardLine("wins", position);
    }

    public static String getLossesLeaderboard(int position) {
        return getLeaderboardLine("losses", position);
    }

    public static String getGamesPlayedLeaderboard(int position) {
        return getLeaderboardLine("games", position);
    }

    public static String getKDRatioLeaderboard(int position) {
        return getLeaderboardLine("kd", position);
    }
}