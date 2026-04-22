package com.zelvera.scoreboard;

import org.bukkit.Bukkit;
//import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scoreboard.*;

import java.lang.reflect.Method;
import java.util.UUID;

public class BridgeFightIntegration {

    private final ScoreboardPlugin plugin;
    private Object bridgeFightPlugin;
    private Method getGameManagerMethod;
    private Class<?> gameManagerClass;
    private Class<?> playerDataClass;
    private Class<?> arenaClass;
    private Class<?> teamClass;

    public BridgeFightIntegration(ScoreboardPlugin plugin) {
        this.plugin = plugin;
        initializeIntegration();
    }

    private void initializeIntegration() {
        Plugin bfPlugin = Bukkit.getPluginManager().getPlugin("BridgeFight");
        if (bfPlugin == null) return;

        try {
            bridgeFightPlugin = bfPlugin;
            Class<?> bfClass = bfPlugin.getClass();

            // Get GameManager
            getGameManagerMethod = bfClass.getMethod("getGameManager");
            Object gameManager = getGameManagerMethod.invoke(bridgeFightPlugin);
            gameManagerClass = gameManager.getClass();

            // Load classes
            playerDataClass = Class.forName("id.hyperionx.bridgefight.PlayerData");
            arenaClass = Class.forName("id.hyperionx.bridgefight.Arena");
            teamClass = Class.forName("id.hyperionx.bridgefight.Team");

            plugin.getLogger().info("Successfully integrated with BridgeFight plugin!");
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to integrate with BridgeFight: " + e.getMessage());
        }
    }

    public void setBridgeFightScoreboard(Player player) {
        if (bridgeFightPlugin == null) {
            plugin.getScoreboardManager().setDefaultScoreboard(player);
            return;
        }

        try {
            Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
            Objective objective = scoreboard.registerNewObjective("bridgefight", "dummy");
            objective.setDisplaySlot(DisplaySlot.SIDEBAR);
            objective.setDisplayName("§6§lBridgeFight");

            Object gameManager = getGameManagerMethod.invoke(bridgeFightPlugin);
            Object playerData = getPlayerData(gameManager, player.getUniqueId());

            if (playerData != null) {
                // Get stats
                int kills = (int) playerDataClass.getMethod("getKills").invoke(playerData);
                int deaths = (int) playerDataClass.getMethod("getDeaths").invoke(playerData);
                int wins = (int) playerDataClass.getMethod("getWins").invoke(playerData);

                // Check if player is in a game
                Object currentArena = getPlayerArena(gameManager, player);
                if (currentArena != null) {
                    // Player is in a game
                    Object team = getPlayerTeam(currentArena, player);
                    String teamName = team != null ? (String) teamClass.getMethod("getColoredName").invoke(team) : "§7No Team";

                    String gameState = getArenaState(currentArena);

                    // Add countdown timer for starting games
                    String timerLine = "";
                    if ("STARTING".equals(gameState)) {
                        try {
                            // Access the countdown field from Arena class
                            java.lang.reflect.Field countdownField = currentArena.getClass().getDeclaredField("countdown");
                            countdownField.setAccessible(true);
                            int countdown = (int) countdownField.get(currentArena);
                            timerLine = "§eStarting: §f" + countdown + "s";
                        } catch (Exception e) {
                            plugin.getLogger().warning("Failed to get countdown timer: " + e.getMessage());
                            timerLine = "§eStarting soon...";
                        }
                    }

                    objective.getScore(teamName).setScore(9);
                    if (!timerLine.isEmpty()) {
                        objective.getScore(timerLine).setScore(8);
                        objective.getScore("§7Game: §f" + gameState).setScore(7);
                        objective.getScore("").setScore(6);
                        objective.getScore("§eKills: §f" + kills).setScore(5);
                        objective.getScore("§eDeaths: §f" + deaths).setScore(4);
                        objective.getScore("§eWins: §f" + wins).setScore(3);
                        objective.getScore(" ").setScore(2);
                        objective.getScore("§6zelvera.net").setScore(1);
                    } else {
                        objective.getScore("§7Game: §f" + gameState).setScore(8);
                        objective.getScore("").setScore(7);
                        objective.getScore("§eKills: §f" + kills).setScore(6);
                        objective.getScore("§eDeaths: §f" + deaths).setScore(5);
                        objective.getScore("§eWins: §f" + wins).setScore(4);
                        objective.getScore(" ").setScore(3);
                        objective.getScore("§6zelvera.net").setScore(2);
                    }
                } else {
                    // Player not in game
                    objective.getScore("§7Not in game").setScore(5);
                    objective.getScore("").setScore(4);
                    objective.getScore("§eKills: §f" + kills).setScore(3);
                    objective.getScore("§eDeaths: §f" + deaths).setScore(2);
                    objective.getScore("§eWins: §f" + wins).setScore(1);
                    objective.getScore("§6zelvera.net").setScore(0);
                }
            } else {
                // No player data
                objective.getScore("§7Loading...").setScore(1);
                objective.getScore("§6zelvera.net").setScore(0);
            }

            player.setScoreboard(scoreboard);
            plugin.getScoreboardManager().storePlayerScoreboard(player.getUniqueId(), scoreboard);

        } catch (Exception e) {
            plugin.getLogger().warning("Error updating BridgeFight scoreboard: " + e.getMessage());
            plugin.getScoreboardManager().setDefaultScoreboard(player);
        }
    }

    private Object getPlayerData(Object gameManager, UUID uuid) throws Exception {
        Method getPlayerDataMethod = gameManagerClass.getMethod("getPlayerData", UUID.class);
        return getPlayerDataMethod.invoke(gameManager, uuid);
    }

    private Object getPlayerArena(Object gameManager, Player player) throws Exception {
        Method getArenasMethod = gameManagerClass.getMethod("getArenas");
        Iterable<?> arenas = (Iterable<?>) getArenasMethod.invoke(gameManager);

        Method hasPlayerMethod = arenaClass.getMethod("hasPlayer", Player.class);
        for (Object arena : arenas) {
            if ((boolean) hasPlayerMethod.invoke(arena, player)) {
                return arena;
            }
        }
        return null;
    }

    private Object getPlayerTeam(Object arena, Player player) throws Exception {
        // Access playerTeams field
        java.lang.reflect.Field playerTeamsField = arenaClass.getDeclaredField("playerTeams");
        playerTeamsField.setAccessible(true);
        @SuppressWarnings("unchecked")
        java.util.Map<UUID, Object> playerTeams = (java.util.Map<UUID, Object>) playerTeamsField.get(arena);
        return playerTeams.get(player.getUniqueId());
    }

    private String getArenaState(Object arena) throws Exception {
        Method getStateMethod = arenaClass.getMethod("getState");
        Object state = getStateMethod.invoke(arena);
        return state.toString();
    }
}