package de.jaunikapauni.axbackpacks.command;

import de.jaunikapauni.axbackpacks.AxBackpacks;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

public class BackpackCommand implements CommandExecutor {
    AxBackpacks reference;
    public BackpackCommand(AxBackpacks reference){
        this.reference = reference;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        Player p = (Player) sender;
        Inventory inv = reference.getPlayerManager().loadPlayerBackpack(p);
        p.openInventory(inv);
        return true;
    }
}
