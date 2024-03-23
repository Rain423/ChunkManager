package cc.sakurarain.chunkmanager;

import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Commands implements CommandExecutor, TabCompleter {

    private ChunkManager chunkManager;
    private List<String> help = new ArrayList<>();
    private HashMap<String, WorldStats> bufferWorldStats = new HashMap<>();


    public Commands(ChunkManager chunkManager) {
        this.chunkManager = chunkManager;
        this.help.add(ChatColor.GREEN + "********************" + ChatColor.GOLD + "ChunkManager" + ChatColor.GREEN + "********************");
        this.help.add(ChatColor.GOLD + "  help  " + ChatColor.WHITE + "显示帮助");
        this.help.add(ChatColor.GOLD + "  show <world>  " + ChatColor.WHITE + "列出已加载区块");
        this.help.add(ChatColor.GOLD + "  load <world> <x> <y>  " + ChatColor.WHITE + "加载指定方块下的区块");
        this.help.add(ChatColor.GOLD + "  unload <world> <x> <y>  " + ChatColor.WHITE + "卸载指定方块下的区块");
        this.help.add(ChatColor.GOLD + "  tp <world> <x> <y>  " + ChatColor.WHITE + "传送到指定坐标");
        this.help.add(ChatColor.GREEN + "********************" + ChatColor.GOLD + "ChunkManager" + ChatColor.GREEN + "********************");
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender.isOp()) {
            String[] bufferArray = strings;
            if (strings.length <= 0) {
                bufferArray = new String[1];
                bufferArray[0] = "help";
            }
            switch (bufferArray[0]) {
                case "help":
                    this.help(commandSender);
                    break;
                case "show":
                    if (bufferArray.length == 1) {
                        if (commandSender instanceof Player) {
                            Player player = (Player) commandSender;
                            this.show(commandSender, player.getWorld().getName());
                        }
                    } else if (bufferArray.length == 2) {
                        this.show(commandSender, bufferArray[1]);
                    }
                    break;
                case "showi":
                    if (bufferArray.length == 2) {
                        this.showi(commandSender, Integer.parseInt(bufferArray[1]));
                    }
                    break;
                case "load":
                    if (bufferArray.length == 4) {
                        this.load(commandSender, bufferArray[1], Integer.parseInt(bufferArray[2]), Integer.parseInt(bufferArray[3]));
                    }
                    break;
                case "unload":
                    if (bufferArray.length == 4) {
                        this.unload(commandSender, bufferArray[1], Integer.parseInt(bufferArray[2]), Integer.parseInt(bufferArray[3]));
                    }
                    break;
                case "tp":
                    if (bufferArray.length == 4) {
                        this.tp(commandSender, bufferArray[1], Integer.parseInt(bufferArray[2]), Integer.parseInt(bufferArray[3]));
                    }
                    break;
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender.isOp()) {
            List<String> list = new ArrayList<>();
            switch (strings.length) {
                case 1:
                    list.add("help");
                    list.add("show");
                    list.add("showi");
                    list.add("load");
                    list.add("unload");
                    list.add("tp");
                    break;
                case 2:
                    if (!strings[0].equalsIgnoreCase("help")) {
                        this.chunkManager.getServer().getWorlds().forEach(world -> {
                            list.add(world.getName());
                        });
                    }
                    break;
            }
            return list;
        }
        return null;
    }

    public void help(CommandSender commandSender) {
        this.help.forEach(h -> {
            commandSender.sendMessage(h);
        });
    }

    public void show(CommandSender commandSender, String worldName) {
        World world = chunkManager.getServer().getWorld(worldName);
        if (world == null) {
            return;
        }

        Chunk[] chunks = world.getLoadedChunks();
        List<Point> points = new ArrayList<>();
        int entities = 0;
        int tiles = 0;
        int index = 0;

        for (Chunk loadedChunk : chunks) {
            int x = loadedChunk.getX() * 16;
            int z = loadedChunk.getZ() * 16;
            int e = loadedChunk.getEntities().length;
            int t = loadedChunk.getTileEntities().length;

            entities += e;
            tiles += t;

            points.add(new Point(index, world.getName(), x, z, e, t));
            index++;
        }

        this.bufferWorldStats.put(commandSender.getName(), new WorldStats(world.getName(), entities, tiles, points.size(), points));
        this.showi(commandSender, 1);
    }

    public void showi(CommandSender commandSender, int index) {
        if (!this.bufferWorldStats.containsKey(commandSender.getName())) {
            return;
        }

        WorldStats worldStats = this.bufferWorldStats.get(commandSender.getName());
        int size = 20;
        int maxIndex = worldStats.chunks / size + (worldStats.chunks % size == 0 ? 0 : 1);
        if (index > maxIndex) {
            index = maxIndex;
        }
        commandSender.sendMessage(ChatColor.YELLOW + " ------ ======= " + ChatColor.GOLD + worldStats.world + ChatColor.YELLOW + " ======= ------ ");
        commandSender.sendMessage(ChatColor.GOLD + " Chunks: " + worldStats.chunks + " Entities: " + worldStats.entities + " Tiles: " + worldStats.tiles);
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;

            for (int i = size * (index - 1); i < size * index; i++) {
                if (i >= worldStats.chunks) {
                    break;
                }
                this.chunkManager.getServer().dispatchCommand(this.chunkManager.getServer().getConsoleSender(), "tellraw " + player.getName() + " " + worldStats.points.get(i).rawText());

            }

            //这是页面按钮
            String text = "[{\"text\":\"" + ChatColor.DARK_GREEN + " ------ <<< " + "\"}," +
                    "{\"text\":\"" + (index == 1 ? ChatColor.GRAY : ChatColor.GOLD) + "上一页" + "\",\"hoverEvent\":{\"action\":\"show_text\",\"value\":\"" + ChatColor.GREEN + (index == 1 ? ">|" : "<<<") + "\"},\"clickEvent\":{\"action\":\"run_command\",\"value\":\"" + "/chunkmanager showi " + (index == 1 ? maxIndex : index - 1) + "\"}}," +
                    "{\"text\":\"" + " " + ChatColor.GREEN + index + ChatColor.GRAY + "/" + ChatColor.GREEN + maxIndex + " " + "\"}," +
                    "{\"text\":\"" + (index == maxIndex ? ChatColor.GRAY : ChatColor.GOLD) + "下一页" + "\",\"hoverEvent\":{\"action\":\"show_text\",\"value\":\"" + ChatColor.GREEN + (index == maxIndex ? "|<" : ">>>") + "\"},\"clickEvent\":{\"action\":\"run_command\",\"value\":\"" + "/chunkmanager showi " + (index == maxIndex ? 1 : index + 1) + "\"}}," +
                    "{\"text\":\"" + ChatColor.DARK_GREEN + " >>> ------ " + "\"}]";
            this.chunkManager.getServer().dispatchCommand(this.chunkManager.getServer().getConsoleSender(), "tellraw " + player.getName() + " " + text);
        } else {

            for (int i = size * (index - 1); i < size * index; i++) {
                if (i >= worldStats.chunks) {
                    break;
                }
                commandSender.sendMessage(worldStats.points.get(i).getText());
            }

            commandSender.sendMessage(ChatColor.DARK_GRAY + " ------ <<< " + (index == 1 ? ChatColor.GRAY : ChatColor.GOLD) + "上一页" + " " + ChatColor.GREEN + index + ChatColor.GRAY + "/" + ChatColor.DARK_GRAY + maxIndex + " " + (index == maxIndex ? ChatColor.GRAY : ChatColor.GOLD) + "下一页" + ChatColor.DARK_GRAY + " >>> ------ ");
        }
    }

    public void load(CommandSender commandSender, String worldName, int x, int z) {
        World world = chunkManager.getServer().getWorld(worldName);
        if (world == null) {
            return;
        }

        world.loadChunk(x / 16, z / 16);
    }

    public void unload(CommandSender commandSender, String worldName, int x, int z) {
        World world = chunkManager.getServer().getWorld(worldName);
        if (world == null) {
            return;
        }

        if (world.isChunkLoaded(x / 16, z / 16)) ;

        world.unloadChunk(x / 16, z / 16);
    }

    public void tp(CommandSender commandSender, String worldName, int x, int z) {
        World world = chunkManager.getServer().getWorld(worldName);
        if (world == null) {
            return;
        }
        if (commandSender instanceof Player) {
            Player player = (Player) commandSender;

            player.teleport(new Location(world, x + 0.5, player.getLocation().getY(), z + 0.5));
        }


    }
}
