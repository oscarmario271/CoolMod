package bosha.coolmod.client;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

public class FriendStorage {

    private static final Gson GSON = new Gson();
    private static final Path FILE =
            Path.of("config", "coolmod_friends.json");

    private static final Type TYPE = new TypeToken<Set<String>>(){}.getType();

    private static Set<String> friends = new HashSet<>();

    public static void add(String name) {
        friends.add(name.toLowerCase());
        save();
    }

    public static void remove(String name) {
        friends.remove(name.toLowerCase());
        save();
    }

    public static Set<String> getAll() {
        return friends;
    }

    public static boolean isFriend(String name) {
        return friends.contains(name.toLowerCase());
    }

    public static void load() {
        try {
            if (Files.exists(FILE)) {
                friends = GSON.fromJson(Files.readString(FILE), TYPE);
            }
        } catch (Exception ignored) {}
    }

    private static void save() {
        try {
            Files.createDirectories(FILE.getParent());
            Files.writeString(FILE, GSON.toJson(friends));
        } catch (IOException ignored) {}
    }
}
