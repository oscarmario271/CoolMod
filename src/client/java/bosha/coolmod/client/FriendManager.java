package bosha.coolmod.client;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class FriendManager {

    private static final Set<UUID> FRIENDS = new HashSet<>();

    public static void add(UUID uuid) {
        FRIENDS.add(uuid);
    }

    public static void remove(UUID uuid) {
        FRIENDS.remove(uuid);
    }

    public static boolean isFriend(UUID uuid) {
        return FRIENDS.contains(uuid);
    }

    public static Set<UUID> getAll() {
        return FRIENDS;
    }
}
