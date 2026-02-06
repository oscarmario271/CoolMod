package bosha.coolmod.client;

import net.minecraft.client.MinecraftClient;

import java.io.*;
import java.util.*;

public class WaypointStorage {

    private static final File FILE = new File("config/coolmod_waypoints.txt");

    // worldKey -> (name -> position)
    private static final Map<String, Map<String, Waypoint>> DATA = new HashMap<>();

    // --------------------
    // Public API
    // --------------------

    public static void addWaypoint(String name, double x, double y, double z) {
        String worldKey = getWorldKey();
        DATA.computeIfAbsent(worldKey, k -> new HashMap<>())
                .put(name.toLowerCase(), new Waypoint(x, y, z));
        save();
    }

    public static boolean removeWaypoint(String name) {
        String worldKey = getWorldKey();
        Map<String, Waypoint> map = DATA.get(worldKey);
        if (map == null) return false;

        boolean removed = map.remove(name.toLowerCase()) != null;
        if (removed) save();
        return removed;
    }
    public static void remove(String worldKey, String name) {
        var world = DATA.get(worldKey);
        if (world == null) return;

        world.remove(name);
        save();
    }


    public static Waypoint getWaypoint(String name) {
        String worldKey = getWorldKey();
        Map<String, Waypoint> map = DATA.get(worldKey);
        if (map == null) return null;
        return map.get(name.toLowerCase());
    }

    public static Set<String> getWaypointNames() {
        String worldKey = getWorldKey();
        Map<String, Waypoint> map = DATA.get(worldKey);
        if (map == null) return Collections.emptySet();
        return map.keySet();
    }

    // --------------------
    // File IO
    // --------------------

    public static void load() {
        DATA.clear();

        if (!FILE.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Format:
                // worldKey|name|x|y|z
                String[] parts = line.split("\\|");
                if (parts.length != 5) continue;

                String worldKey = parts[0];
                String name = parts[1];
                double x = Double.parseDouble(parts[2]);
                double y = Double.parseDouble(parts[3]);
                double z = Double.parseDouble(parts[4]);

                DATA.computeIfAbsent(worldKey, k -> new HashMap<>())
                        .put(name.toLowerCase(), new Waypoint(x, y, z));
            }
        } catch (Exception e) {
            System.err.println("[CoolMod] Failed to load waypoints:");
            e.printStackTrace();
        }
    }

    private static void save() {
        FILE.getParentFile().mkdirs();

        try (PrintWriter writer = new PrintWriter(new FileWriter(FILE))) {
            for (var worldEntry : DATA.entrySet()) {
                String worldKey = worldEntry.getKey();

                for (var wpEntry : worldEntry.getValue().entrySet()) {
                    Waypoint wp = wpEntry.getValue();
                    writer.println(
                            worldKey + "|" +
                                    wpEntry.getKey() + "|" +
                                    wp.x + "|" +
                                    wp.y + "|" +
                                    wp.z
                    );
                }
            }
        } catch (Exception e) {
            System.err.println("[CoolMod] Failed to save waypoints:");
            e.printStackTrace();
        }
    }

    // --------------------
    // World Key Logic (per-world + per-server)
    // --------------------

    private static String getWorldKey() {
        MinecraftClient client = MinecraftClient.getInstance();

        if (client.world == null) return "unknown";

        String dimension = client.world.getRegistryKey().getValue().toString();

        // Multiplayer
        if (client.getCurrentServerEntry() != null) {
            String server = client.getCurrentServerEntry().address.replace(":", "_");
            return "server_" + server + "_" + dimension;
        }

        // Singleplayer
        if (client.getServer() != null) {
            String worldName = client.getServer()
                    .getSaveProperties()
                    .getLevelName();
            return "singleplayer_" + worldName + "_" + dimension;
        }

        return "unknown_" + dimension;
    }

    // --------------------
    // Data class
    // --------------------

    public static class Waypoint {
        public final double x;
        public final double y;
        public final double z;

        public Waypoint(double x, double y, double z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }
    public static Map<String, Map<String, Waypoint>> getAll() {
        return DATA;
    }
}
