package bosha.coolmod.client;

import net.minecraft.text.Text;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class FriendCommands {

    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(
                literal("friend")
                        .then(literal("add")
                                .then(argument("name", StringArgumentType.string())
                                        .executes(ctx -> {
                                            String name = StringArgumentType.getString(ctx, "name");
                                            FriendStorage.add(name);
                                            ctx.getSource().sendFeedback(
                                                    Text.literal("§aAdded friend: §e" + name)
                                            );
                                            return 1;
                                        })
                                )
                        )
                        .then(literal("remove")
                                .then(argument("name", StringArgumentType.string())
                                        .executes(ctx -> {
                                            String name = StringArgumentType.getString(ctx, "name");
                                            FriendStorage.remove(name);
                                            ctx.getSource().sendFeedback(
                                                    Text.literal("§cRemoved friend: §e" + name)
                                            );
                                            return 1;
                                        })
                                )
                        )
                        .then(literal("list")
                                .executes(ctx -> {
                                    var friends = FriendStorage.getAll();
                                    if (friends.isEmpty()) {
                                        ctx.getSource().sendFeedback(
                                                Text.literal("§7You have no friends.")
                                        );
                                    } else {
                                        ctx.getSource().sendFeedback(
                                                Text.literal("§aYour friends:")
                                        );
                                        friends.forEach(f ->
                                                ctx.getSource().sendFeedback(
                                                        Text.literal(" §7- §e" + f)
                                                )
                                        );
                                    }
                                    return 1;
                                })
                        )
        );
    }
}