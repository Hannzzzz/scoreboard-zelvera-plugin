package com.zelvera.scoreboard;

import org.bukkit.plugin.java.JavaPlugin;

public class ScoreboardPlugin extends JavaPlugin {

    private ScoreboardManager scoreboardManager;
    private LeaderboardManager leaderboardManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        getLogger().info("ScoreboardZelvera plugin enabled!");

        // Initialize managers
        scoreboardManager = new ScoreboardManager(this);
        leaderboardManager = new LeaderboardManager(this);

        // Register commands
        getCommand("scoreboard").setExecutor(new ScoreboardCommand(this));
        getCommand("leaderboard").setExecutor(new LeaderboardCommand(this));

        // Register listeners
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);
        getServer().getPluginManager().registerEvents(new StatsMenuListener(this), this);

        // Initialize API
        LeaderboardAPI.setPlugin(this);
    }

    @Override
    public void onDisable() {
        getLogger().info("ScoreboardZelvera plugin disabled!");

        if (scoreboardManager != null) {
            scoreboardManager.cleanup();
        }
    }

    public ScoreboardManager getScoreboardManager() {
        return scoreboardManager;
    }

    public LeaderboardManager getLeaderboardManager() {
        return leaderboardManager;
    }
}