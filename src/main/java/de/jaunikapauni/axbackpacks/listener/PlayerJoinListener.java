package de.jaunikapauni.axbackpacks.listener;

import de.jaunikapauni.axbackpacks.AxBackpacks;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class PlayerJoinListener implements Listener {
    AxBackpacks reference;
    public PlayerJoinListener(AxBackpacks reference){
        this.reference = reference;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e){
        Player p = e.getPlayer();
        UUID uuid = p.getUniqueId();
        Bukkit.getScheduler().runTaskAsynchronously(reference, () -> {
            try(Connection conn = reference.getDatabaseManager().getConnection()){
                try(PreparedStatement ps = conn.prepareStatement("SELECT uuid FROM backpacks WHERE uuid = ?")){
                    ps.setString(1, p.getUniqueId().toString());
                    ResultSet rs = ps.executeQuery();
                    if(!rs.next()){
                        try(PreparedStatement insert = conn.prepareStatement("INSERT INTO backpacks (uuid, inventory) VALUES (?, ?)")){
                            insert.setString(1, p.getUniqueId().toString());
                            insert.setString(2, "");
                            insert.executeUpdate();
                        }
                    }
                }
                String data = reference.getPlayerManager().loadPlayerBackpack(uuid);
                Bukkit.getScheduler().runTask(reference, () -> {
                    reference.getPlayerManager().getPlayerBackpacks().put(uuid, reference.getPlayerManager().createBackpack(data));
                });
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });
    }
}
