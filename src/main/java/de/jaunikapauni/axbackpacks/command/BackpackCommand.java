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
        if(!(sender instanceof Player)){
            sender.sendMessage("Only players can run this command!");
            return true;
        }
        Player p = (Player) sender;
        if(!p.hasPermission("axbackpacks.backpack")){
            p.sendMessage("You don't have the permission! [axbackpacks.backpack]");
            return true;
        }
        Inventory inv = reference.getPlayerManager().getPlayerBackpacks().get(p.getUniqueId());
        p.openInventory(inv);
        return true;
    }
}
