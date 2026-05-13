package de.jaunikapauni.axbackpacks.listener;

import de.jaunikapauni.axbackpacks.AxBackpacks;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

import java.io.IOException;

public class InventoryCloseListener implements Listener {
    AxBackpacks reference;
    public InventoryCloseListener(AxBackpacks reference){
        this.reference = reference;
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) throws IOException {
        if(e.getView().getTitle().equals("Rucksack")){
            Player p = (Player) e.getPlayer();
            Inventory inv = e.getInventory();
            reference.getPlayerManager().setPlayerBackpack(p, inv);
        }
    }
}
