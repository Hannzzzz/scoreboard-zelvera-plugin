package com.zelvera.scoreboard;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

import java.util.HashMap;
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

    private void setDefaultScoreboard(Player player) {
        Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective objective = scoreboard.registerNewObjective("default", "dummy");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName("§6§lZelvera");

        objective.getScore("§7Welcome to").setScore(3);
        objective.getScore("§6Zelvera §7Server").setScore(2);
        objective.getScore("").setScore(1);
        objective.getScore("§eOnline: §f" + Bukkit.getOnlinePlayers().size()).setScore(0);

        player.setScoreboard(scoreboard);
        playerScoreboards.put(player.getUniqueId(), scoreboard);
    }

    public void updateScoreboard(Player player) {
        // Update the scoreboard for a specific player
        if (playerScoreboards.containsKey(player.getUniqueId())) {
            // For now, just refresh
            setScoreboard(player);
        }
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
}