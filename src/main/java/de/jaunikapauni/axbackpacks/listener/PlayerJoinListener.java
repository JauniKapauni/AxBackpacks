package de.jaunikapauni.axbackpacks.listener;

import de.jaunikapauni.axbackpacks.AxBackpacks;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PlayerJoinListener implements Listener {
    AxBackpacks reference;
    public PlayerJoinListener(AxBackpacks reference){
        this.reference = reference;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e){
        Player p = e.getPlayer();
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
                    reference.getPlayerManager().getPlayerBackpacks().put(p.getUniqueId(), reference.getPlayerManager().loadPlayerBackpack(p));
                }
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        });
    }
}
