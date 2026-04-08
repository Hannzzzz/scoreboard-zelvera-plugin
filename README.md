# Scoreboard Zelvera Plugin

A Minecraft scoreboard plugin designed to work seamlessly with the BridgeFight plugin, providing real-time game statistics and information to players.

## Features

- **BridgeFight Integration**: Automatically detects and integrates with the BridgeFight plugin
- **Real-time Updates**: Shows live game statistics including kills, deaths, wins, and team information
- **Game State Display**: Displays current game status (Waiting, Starting, In Progress)
- **Countdown Timers**: Shows game starting countdown when enough players join
- **Team Colors**: Shows team affiliations with proper color coding
- **Lobby Stats Display**: Shows personal statistics even when not in a game
- **Interactive Stats Menu**: Click on stats items in hub plugins to open detailed statistics GUI
- **Leaderboard System**: Track and display top players by various metrics
- **ConditionalCommands Integration**: API for hologram leaderboards and dynamic content
- **Admin Commands**: Toggle scoreboards and reload configuration
- **Fallback Mode**: Works as a basic scoreboard even without BridgeFight

## Countdown Timers

The scoreboard now displays countdown timers for:
- **Game Starting**: Shows remaining seconds until the game begins (when enough players join)
- **Dynamic Display**: Timer appears only during the starting phase and adjusts the scoreboard layout accordingly

## Leaderboard Functionality

The plugin includes a comprehensive leaderboard system that tracks top players across multiple categories:

### Leaderboard Types
- **Kills**: Most total kills
- **Deaths**: Most total deaths
- **Wins**: Most game wins
- **Losses**: Most game losses
- **Games Played**: Most total games participated in
- **K/D Ratio**: Best kill-to-death ratio

### Commands
- `/leaderboard <type> [limit]` - View leaderboards in chat
  - Types: `kills`, `deaths`, `wins`, `losses`, `games`, `kd`
  - Example: `/leaderboard kills 10` shows top 10 players by kills

### ConditionalCommands Integration

For hologram leaderboards, use these placeholders in ConditionalCommands:

```
%scoreboard_leaderboard_kills_1%     # #1 on kills leaderboard
%scoreboard_leaderboard_kills_2%     # #2 on kills leaderboard
%scoreboard_leaderboard_wins_1%      # #1 on wins leaderboard
%scoreboard_leaderboard_kd_1%        # #1 on K/D ratio leaderboard
%scoreboard_leaderboard_games_3%     # #3 on games played leaderboard
```

### API Access

Other plugins can access leaderboard data programmatically:

```java
// Get the #1 player on kills leaderboard
String topKills = LeaderboardAPI.getKillsLeaderboard(1);

// Get the #5 player on wins leaderboard
String topWins = LeaderboardAPI.getWinsLeaderboard(5);

// General method
String line = LeaderboardAPI.getLeaderboardLine("kills", 1);
```

## Installation

1. Place the `ScoreboardZelvera.jar` in your server's `plugins` folder
2. If you have the BridgeFight plugin, place it in the same folder for full integration
3. Restart your server

## Stats Menu Integration

The plugin includes a stats menu system that can integrate with hub plugins that have custom join items. When players click on items with names containing "Stats", "Player Stats", "My Stats", or "Statistics", it will open a detailed statistics GUI showing:

- **Kills**: Total number of player kills
- **Deaths**: Total number of deaths
- **K/D Ratio**: Kill-to-death ratio
- **Wins**: Total games won
- **Losses**: Total games lost
- **Win Rate**: Percentage of games won
- **Games Played**: Total games participated in

### Customization

You can customize which items trigger the stats menu by modifying the item name checks in `StatsMenuListener.java`. The current implementation looks for items with display names containing common stats-related terms.

## Commands

- `/scoreboard toggle` - Toggle your scoreboard on/off
- `/scoreboard update` - Manually refresh your scoreboard
- `/scoreboard reload` - Reload configuration (admin only)
- `/leaderboard <type> [limit]` - View BridgeFight leaderboards
  - Types: `kills`, `deaths`, `wins`, `losses`, `games`, `kd`
  - Example: `/leaderboard kills 10`

## Permissions

- `scoreboard.admin` - Allows admin commands for scoreboard (default: op)
- `scoreboard.leaderboard` - Allows viewing leaderboards (default: true)

## Permissions

- `scoreboard.admin` - Access to admin commands (default: op)

## Compatibility

- **Minecraft Version**: 1.8.8
- **Server Software**: Spigot/Paper
- **Required Plugins**: None (optional BridgeFight integration)
- **Java Version**: 8+

## Configuration

The plugin uses the default Minecraft scoreboard system and doesn't require additional configuration files. All settings are handled automatically.

## Development

This plugin is built with Maven and uses the Spigot API. To build from source:

```bash
mvn clean package
```

The compiled JAR will be in the `target` folder.

## Support

For issues or feature requests, please check the BridgeFight plugin compatibility and ensure both plugins are up to date.