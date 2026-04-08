package com.zelvera.scoreboard;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class LeaderboardManager {

    private final ScoreboardPlugin plugin;

    public LeaderboardManager(ScoreboardPlugin plugin) {
        this.plugin = plugin;
    }

    public enum LeaderboardType {
        KILLS("Most Kills"),
        DEATHS("Most Deaths"),
        WINS("Most Wins"),
        LOSSES("Most Losses"),
        GAMES_PLAYED("Most Games Played"),
        KD_RATIO("Best K/D Ratio");

        private final String displayName;

        LeaderboardType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public List<Map.Entry<String, Object>> getLeaderboard(LeaderboardType type, int limit) {
        if (!Bukkit.getPluginManager().isPluginEnabled("BridgeFight")) {
            plugin.getLogger().warning("BridgeFight plugin not found! Leaderboard unavailable.");
            return new ArrayList<>();
        }

        try {
            Object gameManager = getGameManager();
            if (gameManager == null) {
                plugin.getLogger().warning("Failed to access BridgeFight GameManager!");
                return new ArrayList<>();
            }

            // Get all player data
            Map<String, Object> playerStats = getAllPlayerStats(gameManager);

            if (playerStats.isEmpty()) {
                plugin.getLogger().info("No player statistics found for leaderboard.");
                return new ArrayList<>();
            }

            // Sort based on type
            List<Map.Entry<String, Object>> sorted = playerStats.entrySet().stream()
                .sorted((a, b) -> {
                    try {
                        Object statsA = a.getValue();
                        Object statsB = b.getValue();
                        Class<?> playerDataClass = Class.forName("id.hyperionx.bridgefight.PlayerData");

                        switch (type) {
                            case KILLS:
                                return Integer.compare(
                                    (int) playerDataClass.getMethod("getKills").invoke(statsB),
                                    (int) playerDataClass.getMethod("getKills").invoke(statsA)
                                );
                            case DEATHS:
                                return Integer.compare(
                                    (int) playerDataClass.getMethod("getDeaths").invoke(statsB),
                                    (int) playerDataClass.getMethod("getDeaths").invoke(statsA)
                                );
                            case WINS:
                                return Integer.compare(
                                    (int) playerDataClass.getMethod("getWins").invoke(statsB),
                                    (int) playerDataClass.getMethod("getWins").invoke(statsA)
                                );
                            case LOSSES:
                                return Integer.compare(
                                    (int) playerDataClass.getMethod("getLosses").invoke(statsB),
                                    (int) playerDataClass.getMethod("getLosses").invoke(statsA)
                                );
                            case GAMES_PLAYED:
                                int winsA = (int) playerDataClass.getMethod("getWins").invoke(statsA);
                                int lossesA = (int) playerDataClass.getMethod("getLosses").invoke(statsA);
                                int totalA = winsA + lossesA;

                                int winsB = (int) playerDataClass.getMethod("getWins").invoke(statsB);
                                int lossesB = (int) playerDataClass.getMethod("getLosses").invoke(statsB);
                                int totalB = winsB + lossesB;

                                return Integer.compare(totalB, totalA);
                            case KD_RATIO:
                                int killsA = (int) playerDataClass.getMethod("getKills").invoke(statsA);
                                int deathsA = (int) playerDataClass.getMethod("getDeaths").invoke(statsA);
                                double ratioA = deathsA == 0 ? killsA : (double) killsA / deathsA;

                                int killsB = (int) playerDataClass.getMethod("getKills").invoke(statsB);
                                int deathsB = (int) playerDataClass.getMethod("getDeaths").invoke(statsB);
                                double ratioB = deathsB == 0 ? killsB : (double) killsB / deathsB;

                                return Double.compare(ratioB, ratioA);
                            default:
                                return 0;
                        }
                    } catch (Exception e) {
                        plugin.getLogger().warning("Error comparing players for leaderboard: " + e.getMessage());
                        return 0;
                    }
                })
                .limit(limit)
                .collect(Collectors.toList());

            return sorted;

        } catch (Exception e) {
            plugin.getLogger().warning("Failed to generate leaderboard: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    public String formatLeaderboardEntry(LeaderboardType type, int rank, String playerName, Object playerData) {
        try {
            Class<?> playerDataClass = Class.forName("id.hyperionx.bridgefight.PlayerData");

            String value = "";
            switch (type) {
                case KILLS:
                    value = String.valueOf(playerDataClass.getMethod("getKills").invoke(playerData));
                    break;
                case DEATHS:
                    value = String.valueOf(playerDataClass.getMethod("getDeaths").invoke(playerData));
                    break;
                case WINS:
                    value = String.valueOf(playerDataClass.getMethod("getWins").invoke(playerData));
                    break;
                case LOSSES:
                    value = String.valueOf(playerDataClass.getMethod("getLosses").invoke(playerData));
                    break;
                case GAMES_PLAYED:
                    int wins = (int) playerDataClass.getMethod("getWins").invoke(playerData);
                    int losses = (int) playerDataClass.getMethod("getLosses").invoke(playerData);
                    value = String.valueOf(wins + losses);
                    break;
                case KD_RATIO:
                    int kills = (int) playerDataClass.getMethod("getKills").invoke(playerData);
                    int deaths = (int) playerDataClass.getMethod("getDeaths").invoke(playerData);
                    double ratio = deaths == 0 ? kills : Math.round((double) kills / deaths * 100.0) / 100.0;
                    value = String.valueOf(ratio);
                    break;
            }

            String color = getRankColor(rank);
            return color + "#" + rank + " §f" + playerName + " §7- §e" + value;

        } catch (Exception e) {
            return "§cError loading data";
        }
    }

    private String getRankColor(int rank) {
        switch (rank) {
            case 1: return "§6§l"; // Gold
            case 2: return "§f§l"; // White
            case 3: return "§c§l"; // Red
            default: return "§7";  // Gray
        }
    }

    private Object getGameManager() throws Exception {
        if (!Bukkit.getPluginManager().isPluginEnabled("BridgeFight")) return null;

        Object bfPlugin = Bukkit.getPluginManager().getPlugin("BridgeFight");
        Class<?> bfClass = bfPlugin.getClass();
        return bfClass.getMethod("getGameManager").invoke(bfPlugin);
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> getAllPlayerStats(Object gameManager) throws Exception {
        Class<?> gameManagerClass = gameManager.getClass();
        java.lang.reflect.Field playerDataField = gameManagerClass.getDeclaredField("playerData");
        playerDataField.setAccessible(true);
        Map<UUID, Object> playerDataMap = (Map<UUID, Object>) playerDataField.get(gameManager);

        Map<String, Object> result = new HashMap<>();
        for (Map.Entry<UUID, Object> entry : playerDataMap.entrySet()) {
            Player player = Bukkit.getPlayer(entry.getKey());
            if (player != null) {
                result.put(player.getName(), entry.getValue());
            }
        }

        return result;
    }

    // Method for ConditionalCommands plugin integration
    public String getLeaderboardLine(LeaderboardType type, int position) {
        List<Map.Entry<String, Object>> leaderboard = getLeaderboard(type, position);
        if (leaderboard.size() >= position) {
            Map.Entry<String, Object> entry = leaderboard.get(position - 1);
            return formatLeaderboardEntry(type, position, entry.getKey(), entry.getValue());
        }
        return "§7#" + position + " §8- §7No player";
    }
}