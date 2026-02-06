package bosha.coolmod.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class CoolModConfig {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH =
            Path.of("config", "coolmod.json");

    // ===== SETTINGS =====
    public static boolean friendMarkerEnabled = true;
    public static int refreshTicks = 5;          // how often particles spawn
    public static int pillarRadius = 1;
    public static int minDistance = 48;
    public static int heightAboveHead = 1;
    public static int pillarHeight = 16;
    public static int particleCount = 160;


    // ===== FILE MODEL =====
    private static class ConfigData {
        boolean friendMarkerEnabled = true;
        public static int refreshTicks = 5;          // how often particles spawn
        public static int pillarRadius = 1;
        public static int minDistance = 48;
        public static int heightAboveHead = 1;
        public static int pillarHeight = 16;
        public static int particleCount = 160;

    }

    // ===== LOAD =====
    public static void load() {
        try {
            if (!Files.exists(CONFIG_PATH)) {
                save(); // create default file
                return;
            }

            String json = Files.readString(CONFIG_PATH);
            ConfigData data = GSON.fromJson(json, ConfigData.class);

            friendMarkerEnabled = data.friendMarkerEnabled;
            refreshTicks = data.refreshTicks;
            pillarRadius = data.pillarRadius;
            minDistance = data.minDistance;
            heightAboveHead = data.heightAboveHead;
            pillarHeight = data.pillarHeight;
            particleCount = data.particleCount;

            System.out.println("[CoolMod] Config loaded.");
        } catch (Exception e) {
            System.err.println("[CoolMod] Failed to load config!");
            e.printStackTrace();
        }
    }

    // ===== SAVE =====
    public static void save() {
        try {
            Files.createDirectories(CONFIG_PATH.getParent());

            ConfigData data = new ConfigData();
            data.friendMarkerEnabled = friendMarkerEnabled;

            String json = GSON.toJson(data);
            Files.writeString(CONFIG_PATH, json);

            System.out.println("[CoolMod] Config saved.");
        } catch (IOException e) {
            System.err.println("[CoolMod] Failed to save config!");
            e.printStackTrace();
        }
    }
}
