package de.jaunikapauni.axbackpacks.manager;

import de.jaunikapauni.axbackpacks.AxBackpacks;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;

public class PlayerManager {
    AxBackpacks reference;
    public PlayerManager(AxBackpacks reference){
        this.reference = reference;
    }

    public String serializeInventory(Inventory inv) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try(BukkitObjectOutputStream boos = new BukkitObjectOutputStream(baos)){
            boos.writeInt(inv.getSize());
            for(ItemStack item : inv.getContents()){
                boos.writeObject(item);
            }
        }
        return Base64.getEncoder().encodeToString(baos.toByteArray());
    }

    public void setPlayerBackpack(Player p, Inventory inv) throws IOException {
        String serializedInv = serializeInventory(inv);
        Bukkit.getScheduler().runTaskAsynchronously(reference, () -> {
            try(Connection conn = reference.getDatabaseManager().getConnection()){
                try(PreparedStatement ps = conn.prepareStatement("UPDATE backpacks SET inventory = ? WHERE uuid = ?")){
                    ps.setString(1, serializedInv);
                    ps.setString(2, p.getUniqueId().toString());
                    ps.executeUpdate();
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void deserializeInventory(Inventory inv, String data){
        byte[] bytes = Base64.getDecoder().decode(data);
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        try(BukkitObjectInputStream bois = new BukkitObjectInputStream(bais)){
            int size = bois.readInt();
            ItemStack[] items = new ItemStack[size];
            for(int i = 0; i < size; i++){
                items[i] = (ItemStack) bois.readObject();
            }
            inv.setContents(items);
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public Inventory loadPlayerBackpack(Player p){
        Inventory inv = Bukkit.createInventory(null, 27, "Rucksack");
        Bukkit.getScheduler().runTaskAsynchronously(reference, () -> {
            try(Connection conn = reference.getDatabaseManager().getConnection()){
                try(PreparedStatement ps = conn.prepareStatement("SELECT inventory FROM backpacks WHERE uuid = ?")){
                    ps.setString(1, p.getUniqueId().toString());
                    ResultSet rs = ps.executeQuery();
                    if(rs.next()){
                        String data = rs.getString("inventory");
                        if(data != null && !data.isEmpty()){
                            deserializeInventory(inv, data);
                        }
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
        return inv;
    }
}
