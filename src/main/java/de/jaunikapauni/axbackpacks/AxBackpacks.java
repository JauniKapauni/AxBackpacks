package de.jaunikapauni.axbackpacks;

import de.jaunikapauni.axbackpacks.command.BackpackCommand;
import de.jaunikapauni.axbackpacks.listener.InventoryCloseListener;
import de.jaunikapauni.axbackpacks.listener.PlayerJoinListener;
import de.jaunikapauni.axbackpacks.listener.PlayerQuitListener;
import de.jaunikapauni.axbackpacks.manager.DatabaseManager;
import de.jaunikapauni.axbackpacks.manager.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class AxBackpacks extends JavaPlugin {
    DatabaseManager databaseManager;
    public DatabaseManager getDatabaseManager(){
        return databaseManager;
    }
    PlayerManager playerManager;
    public PlayerManager getPlayerManager(){
        return playerManager;
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        saveDefaultConfig();
        databaseManager = new DatabaseManager(this);
        playerManager = new PlayerManager(this);
        try{
            if(databaseManager.initDatabaseTable1() == false){
                getLogger().severe("Error creating table!");
                Bukkit.getServer().shutdown();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        getCommand("backpack").setExecutor(new BackpackCommand(this));
        getCommand("bp").setExecutor(new BackpackCommand(this));
        getServer().getPluginManager().registerEvents(new InventoryCloseListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerQuitListener(this), this);
        getLogger().info("");
        getLogger().info("----------------------------------------");
        getLogger().info("Name: " + getName());
        getLogger().info("Version: " + getDescription().getVersion());
        getLogger().info(String.join("Authors: " + ", ", getDescription().getAuthors()));
        getLogger().info("----------------------------------------");
        getLogger().info("");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        if(playerManager != null){
            getLogger().info("Saving backpacks");
            playerManager.saveAllBackpacks();
            getLogger().info("Backpacks saved");
        }
        if(databaseManager != null){
            databaseManager.close();
        }
    }
}
