package de.jaunikapauni.axbackpacks.listener;

import de.jaunikapauni.axbackpacks.AxBackpacks;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;

import java.io.IOException;

public class PlayerQuitListener implements Listener {

    AxBackpacks reference;
    public PlayerQuitListener(AxBackpacks reference){
        this.reference = reference;
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) throws IOException {
        Player p = e.getPlayer();
        Inventory inv = reference.getPlayerManager().getPlayerBackpacks().remove(p.getUniqueId());
        if(inv == null){
            return;
        }
        Bukkit.getScheduler().runTaskAsynchronously(reference, () -> {
            try {
                String data = reference.getPlayerManager().serializeInventory(inv);
                reference.getPlayerManager().savePlayerBackpack(p.getUniqueId(), data);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
    }
}
