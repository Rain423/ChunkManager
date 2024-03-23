package cc.sakurarain.chunkmanager;

import org.bukkit.ChatColor;

public class Point {

    public int id = 0;
    public String world;
    public int x = 0;
    public int z = 0;
    public int entities = 0;
    public int tiles = 0;

    public Point() {
    }

    public Point(int id, String worldName, int x, int z, int entities, int tiles) {
        this.id = id;
        this.world = worldName;
        this.x = x;
        this.z = z;
        this.entities = entities;
        this.tiles = tiles;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setWorld(String world) {
        this.world = world;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setZ(int z) {
        this.z = z;
    }

    public void setEntities(int entities) {
        this.entities = entities;
    }

    public void setTiles(int tiles) {
        this.tiles = tiles;
    }

    public int getId() {
        return this.id;
    }

    public String getWorld() {
        return this.world;
    }

    public int getX() {
        return this.x;
    }

    public int getZ() {
        return this.z;
    }

    public int getEntities() {
        return this.entities;
    }

    public int getTiles() {
        return this.tiles;
    }

    public String getText() {
        return " " + ChatColor.GOLD + this.id + "." + ChatColor.DARK_AQUA + " x: " + this.x + "  y: " + this.z + "  Entity: " + this.entities + "  Tile: " + this.tiles;
    }

    public String rawText() {
        return "{\"text\":\"" + this.getText() + "\"," +
                "\"hoverEvent\":{\"action\":\"show_text\",\"value\":\"" + ChatColor.GOLD + this.id + ": " + ChatColor.GREEN + "点击传送" + "\"}," +
                "\"clickEvent\":{\"action\":\"run_command\",\"value\":\"" + "/chunkmanager tp " + this.world + " " + this.x + " " + this.z + "\"}}";
    }
}
