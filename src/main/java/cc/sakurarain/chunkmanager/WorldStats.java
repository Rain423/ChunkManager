package cc.sakurarain.chunkmanager;

import java.util.List;

public class WorldStats {
    public String world;
    public int entities = 0;
    public int tiles = 0;
    public int chunks = 0;
    public List<Point> points;

    public WorldStats() {
    }

    public WorldStats(String world, int entities, int tiles, int chunks, List<Point> points) {
        this.world = world;
        this.entities = entities;
        this.tiles = tiles;
        this.chunks = chunks;
        this.points = points;
    }
}
