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
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerManager {
    AxBackpacks reference;

    public PlayerManager(AxBackpacks reference) {
        this.reference = reference;
    }

    Map<UUID, Inventory> playerBackpacks = new ConcurrentHashMap<>();

    public Map<UUID, Inventory> getPlayerBackpacks() {
        return playerBackpacks;
    }

    public String serializeInventory(Inventory inv) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (BukkitObjectOutputStream boos = new BukkitObjectOutputStream(baos)) {
            boos.writeInt(inv.getSize());
            for (ItemStack item : inv.getContents()) {
                boos.writeObject(item);
            }
        }
        return Base64.getEncoder().encodeToString(baos.toByteArray());
    }

    public void savePlayerBackpack(UUID uuid, String serializedInventory) throws IOException {
        try (Connection conn = reference.getDatabaseManager().getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement("UPDATE backpacks SET inventory = ? WHERE uuid = ?")) {
                ps.setString(1, serializedInventory);
                ps.setString(2, uuid.toString());
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public ItemStack[] deserializeInventory(String data) {
        byte[] bytes = Base64.getDecoder().decode(data);
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        try (BukkitObjectInputStream bois = new BukkitObjectInputStream(bais)) {
            int size = bois.readInt();
            ItemStack[] items = new ItemStack[size];
            for (int i = 0; i < size; i++) {
                items[i] = (ItemStack) bois.readObject();
            }
            return items;
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public String loadPlayerBackpack(UUID uuid) {
        try (Connection conn = reference.getDatabaseManager().getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement("SELECT inventory FROM backpacks WHERE uuid = ?")) {
                ps.setString(1, uuid.toString());
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    return rs.getString("inventory");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return "";
    }

    public Inventory createBackpack(String data){
        Inventory inv = Bukkit.createInventory(null, 27, "Rucksack");
        if(data != null && !data.isEmpty()){
            ItemStack[] items = deserializeInventory(data);
            inv.setContents(items);
        }
        return inv;
    }

    public void saveAllBackpacks(){
        for(Map.Entry<UUID, Inventory> entry : playerBackpacks.entrySet()){
            UUID uuid = entry.getKey();
            Inventory inv = entry.getValue();
            try{
                String data = serializeInventory(inv);
                savePlayerBackpack(uuid, data);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
