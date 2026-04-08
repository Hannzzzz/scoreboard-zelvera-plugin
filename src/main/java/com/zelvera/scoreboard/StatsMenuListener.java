package com.zelvera.scoreboard;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.UUID;

public class StatsMenuListener implements Listener {

    private final ScoreboardPlugin plugin;

    public StatsMenuListener(ScoreboardPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;

        Player player = (Player) event.getWhoClicked();
        ItemStack clickedItem = event.getCurrentItem();

        if (clickedItem == null || !clickedItem.hasItemMeta()) return;

        ItemMeta meta = clickedItem.getItemMeta();
        if (!meta.hasDisplayName()) return;

        String displayName = ChatColor.stripColor(meta.getDisplayName());

        // Check for stats item (customize this based on your hub plugin's item names)
        if (displayName.equalsIgnoreCase("Player Stats") ||
            displayName.equalsIgnoreCase("My Stats") ||
            displayName.equalsIgnoreCase("Statistics") ||
            displayName.contains("Stats")) {

            event.setCancelled(true);
            openStatsMenu(player);
        }

        // Handle close button in stats menu
        if (event.getView().getTitle().equals("§6§lYour BridgeFight Stats") &&
            displayName.equalsIgnoreCase("Close")) {

            event.setCancelled(true);
            player.closeInventory();
        }
    }

    public void openStatsMenu(Player player) {
        Inventory statsMenu = Bukkit.createInventory(null, 27, "§6§lYour BridgeFight Stats");

        // Get player data
        Object gameManager = null;
        Object playerData = null;

        try {
            if (plugin.getServer().getPluginManager().isPluginEnabled("BridgeFight")) {
                gameManager = plugin.getServer().getPluginManager()
                    .getPlugin("BridgeFight").getClass()
                    .getMethod("getGameManager")
                    .invoke(plugin.getServer().getPluginManager().getPlugin("BridgeFight"));

                Class<?> playerDataClass = Class.forName("id.hyperionx.bridgefight.PlayerData");
                playerData = gameManager.getClass()
                    .getMethod("getPlayerData", UUID.class)
                    .invoke(gameManager, player.getUniqueId());
            }
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to get player data for stats menu: " + e.getMessage());
        }

        // Create stat items
        if (playerData != null) {
            try {
                Class<?> playerDataClass = Class.forName("id.hyperionx.bridgefight.PlayerData");

                int kills = (int) playerDataClass.getMethod("getKills").invoke(playerData);
                int deaths = (int) playerDataClass.getMethod("getDeaths").invoke(playerData);
                int wins = (int) playerDataClass.getMethod("getWins").invoke(playerData);
                int losses = (int) playerDataClass.getMethod("getLosses").invoke(playerData);
                int gamesPlayed = wins + losses;

                double kdRatio = deaths == 0 ? kills : Math.round((double) kills / deaths * 100.0) / 100.0;
                double winRate = gamesPlayed == 0 ? 0 : Math.round((double) wins / gamesPlayed * 100.0);

                // Kills item
                ItemStack killsItem = new ItemStack(Material.DIAMOND_SWORD);
                ItemMeta killsMeta = killsItem.getItemMeta();
                killsMeta.setDisplayName("§c§lKills");
                killsMeta.setLore(Arrays.asList(
                    "§7Total: §f" + kills,
                    "",
                    "§7Your kill count in BridgeFight"
                ));
                killsItem.setItemMeta(killsMeta);
                statsMenu.setItem(10, killsItem);

                // Deaths item
                ItemStack deathsItem = new ItemStack(Material.SKELETON_SKULL);
                ItemMeta deathsMeta = deathsItem.getItemMeta();
                deathsMeta.setDisplayName("§4§lDeaths");
                deathsMeta.setLore(Arrays.asList(
                    "§7Total: §f" + deaths,
                    "",
                    "§7Times you've been eliminated"
                ));
                deathsItem.setItemMeta(deathsMeta);
                statsMenu.setItem(11, deathsItem);

                // K/D Ratio
                ItemStack kdItem = new ItemStack(Material.COMPASS);
                ItemMeta kdMeta = kdItem.getItemMeta();
                kdMeta.setDisplayName("§e§lK/D Ratio");
                kdMeta.setLore(Arrays.asList(
                    "§7Ratio: §f" + kdRatio,
                    "",
                    "§7Kills divided by deaths"
                ));
                kdItem.setItemMeta(kdMeta);
                statsMenu.setItem(12, kdItem);

                // Wins
                ItemStack winsItem = new ItemStack(Material.GOLDEN_APPLE);
                ItemMeta winsMeta = winsItem.getItemMeta();
                winsMeta.setDisplayName("§6§lWins");
                winsMeta.setLore(Arrays.asList(
                    "§7Total: §f" + wins,
                    "",
                    "§7Games you've won"
                ));
                winsItem.setItemMeta(winsMeta);
                statsMenu.setItem(14, winsItem);

                // Losses
                ItemStack lossesItem = new ItemStack(Material.BARRIER);
                ItemMeta lossesMeta = lossesItem.getItemMeta();
                lossesMeta.setDisplayName("§c§lLosses");
                lossesMeta.setLore(Arrays.asList(
                    "§7Total: §f" + losses,
                    "",
                    "§7Games you've lost"
                ));
                lossesItem.setItemMeta(lossesMeta);
                statsMenu.setItem(15, lossesItem);

                // Win Rate
                ItemStack winRateItem = new ItemStack(Material.EXPERIENCE_BOTTLE);
                ItemMeta winRateMeta = winRateItem.getItemMeta();
                winRateMeta.setDisplayName("§a§lWin Rate");
                winRateMeta.setLore(Arrays.asList(
                    "§7Rate: §f" + winRate + "%",
                    "",
                    "§7Percentage of games won"
                ));
                winRateItem.setItemMeta(winRateMeta);
                statsMenu.setItem(16, winRateItem);

                // Games Played
                ItemStack gamesItem = new ItemStack(Material.BOOK);
                ItemMeta gamesMeta = gamesItem.getItemMeta();
                gamesMeta.setDisplayName("§b§lGames Played");
                gamesMeta.setLore(Arrays.asList(
                    "§7Total: §f" + gamesPlayed,
                    "",
                    "§7Total games participated in"
                ));
                gamesItem.setItemMeta(gamesMeta);
                statsMenu.setItem(13, gamesItem);

            } catch (Exception e) {
                plugin.getLogger().warning("Error creating stats items: " + e.getMessage());
            }
        } else {
            // No data available
            ItemStack noDataItem = new ItemStack(Material.BARRIER);
            ItemMeta noDataMeta = noDataItem.getItemMeta();
            noDataMeta.setDisplayName("§c§lNo Data Available");
            noDataMeta.setLore(Arrays.asList(
                "§7Could not load your statistics",
                "§7Please try again later"
            ));
            noDataItem.setItemMeta(noDataMeta);
            statsMenu.setItem(13, noDataItem);
        }

        // Close button
        ItemStack closeItem = new ItemStack(Material.REDSTONE);
        ItemMeta closeMeta = closeItem.getItemMeta();
        closeMeta.setDisplayName("§c§lClose");
        closeItem.setItemMeta(closeMeta);
        statsMenu.setItem(26, closeItem);

        player.openInventory(statsMenu);
    }
}