package bosha.coolmod.client;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.*;

public class WaypointCommands {

    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {

        dispatcher.register(literal("tpway")
                // ---------------- ADD ----------------
                .then(literal("add")
                        .then(argument("name", StringArgumentType.string())
                                .executes(ctx -> {
                                    String name = StringArgumentType.getString(ctx, "name");
                                    MinecraftClient client = MinecraftClient.getInstance();

                                    if (client.player == null) return 0;

                                    double x = client.player.getX();
                                    double y = client.player.getY();
                                    double z = client.player.getZ();

                                    WaypointStorage.addWaypoint(name, x, y, z);

                                    client.player.sendMessage(
                                            Text.literal("§aWaypoint added: §e" + name),
                                            false
                                    );
                                    return 1;
                                })
                        )
                )

                // ---------------- REMOVE ----------------
                .then(literal("remove")
                        .then(argument("name", StringArgumentType.string())
                                .executes(ctx -> {
                                    String name = StringArgumentType.getString(ctx, "name");
                                    MinecraftClient client = MinecraftClient.getInstance();

                                    if (client.player == null) return 0;

                                    boolean removed = WaypointStorage.removeWaypoint(name);

                                    if (removed) {
                                        client.player.sendMessage(
                                                Text.literal("§cWaypoint removed: §e" + name),
                                                false
                                        );
                                    } else {
                                        client.player.sendMessage(
                                                Text.literal("§7Waypoint not found."),
                                                false
                                        );
                                    }
                                    return 1;
                                })
                        )
                )

                // ---------------- TP ----------------
                .then(literal("tp")
                        .then(argument("name", StringArgumentType.string())
                                .executes(ctx -> {
                                    String name = StringArgumentType.getString(ctx, "name");
                                    MinecraftClient client = MinecraftClient.getInstance();

                                    if (client.player == null) return 0;

                                    WaypointStorage.Waypoint wp = WaypointStorage.getWaypoint(name);

                                    if (wp == null) {
                                        client.player.sendMessage(
                                                Text.literal("§7Waypoint not found."),
                                                false
                                        );
                                        return 0;
                                    }

                                    // Client-side teleport (singleplayer or servers that allow it)
                                    client.player.setPosition(wp.x, wp.y, wp.z);

                                    client.player.sendMessage(
                                            Text.literal("§bTeleported to §e" + name),
                                            false
                                    );
                                    return 1;
                                })
                        )
                )
        );
    }
}
