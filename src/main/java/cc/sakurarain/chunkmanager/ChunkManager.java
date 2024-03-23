package cc.sakurarain.chunkmanager;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public final class ChunkManager extends JavaPlugin implements CommandExecutor {

    @Override
    public void onEnable() {
        // Plugin startup logic
        Commands commands = new Commands(this);
        this.getServer().getPluginCommand("chunkmanager").setExecutor(commands);
        this.getServer().getPluginCommand("chunkmanager").setTabCompleter(commands);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

}
