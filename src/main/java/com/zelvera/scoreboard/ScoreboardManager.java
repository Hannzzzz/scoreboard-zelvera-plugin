package com.zelvera.scoreboard;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ScoreboardManager {

    private final ScoreboardPlugin plugin;
    private final Map<UUID, org.bukkit.scoreboard.Scoreboard> playerScoreboards = new HashMap<>();
    private final Map<UUID, Integer> playerTaskIds = new HashMap<>();
    private BridgeFightIntegration bridgeFightIntegration;

    public ScoreboardManager(ScoreboardPlugin plugin) {
        this.plugin = plugin;
        if (Bukkit.getPluginManager().isPluginEnabled("BridgeFight")) {
            bridgeFightIntegration = new BridgeFightIntegration(plugin);
        }
    }

    public void setScoreboard(Player player) {
        // Check if BridgeFight is available
        if (bridgeFightIntegration != null) {
            bridgeFightIntegration.setBridgeFightScoreboard(player);
        } else {
            setDefaultScoreboard(player);
        }
    }

    public void storePlayerScoreboard(UUID uuid, org.bukkit.scoreboard.Scoreboard scoreboard) {
        playerScoreboards.put(uuid, scoreboard);
    }

    public void setDefaultScoreboard(Player player) {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective objective = scoreboard.registerNewObjective("default", "dummy");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName("§6§lZelvera");

        int score = 3;

        objective.getScore("§7Welcome to").setScore(score--);
        objective.getScore("§6ZelveraMC Server").setScore(score--);
        objective.getScore(" ").setScore(score--);
        objective.getScore("§eOnline: " + Bukkit.getOnlinePlayers().size()).setScore(score--);

        player.setScoreboard(scoreboard);
        playerScoreboards.put(player.getUniqueId(), scoreboard);
    }

    public void updateScoreboard(Player player) {
        // Update the scoreboard for a specific player
        if (playerScoreboards.containsKey(player.getUniqueId())) {
            // For now, just refresh
            setScoreboard(player);
        }
        List<String> lines = plugin.getConfig().getStringList("scoreboard.lines");
        int score = lines.size();
        for (String line : lines) {
            line = line
                    .replace("%player%", player.getName())
                    .replace("%map%", "§Arena1")
                    .replace("%team%", "§TeamA")
                    ;
        }
    }

    private Object getConfig() {
        // this method should return the plugin's configuration object, 
        // but since we don't have access to it here, we'll just throw an exception for now
        throw new UnsupportedOperationException("Unimplemented method 'getConfig'");
    }

    public void removeScoreboard(Player player) {
        player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
        playerScoreboards.remove(player.getUniqueId());

        // Cancel any update tasks
        Integer taskId = playerTaskIds.remove(player.getUniqueId());
        if (taskId != null) {
            Bukkit.getScheduler().cancelTask(taskId);
        }
    }

    public org.bukkit.scoreboard.Scoreboard getPlayerScoreboard(UUID uuid) {
        return playerScoreboards.get(uuid);
    }

    public void cleanup() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            removeScoreboard(player);
        }
        playerScoreboards.clear();
        playerTaskIds.clear();
    }
}