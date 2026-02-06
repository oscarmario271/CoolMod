package bosha.coolmod.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import org.lwjgl.glfw.GLFW;

public class CoolModClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {

        // Load friends FIRST
        FriendStorage.load();

        WaypointStorage.load();

        // Register client commands
        ClientCommandRegistrationCallback.EVENT.register(
                (dispatcher, registryAccess) ->
                        FriendCommands.register(dispatcher)
        );

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            WaypointCommands.register(dispatcher);
        });

        CoolModConfig.load();

        FriendMarker.init();

        // Register the keybind
        openConfigKey = KeyBindingHelper.registerKeyBinding(
                new KeyBinding(
                        "key.coolmod.open_config",
                        GLFW.GLFW_KEY_O,
                        net.minecraft.client.option.KeyBinding.Category.MISC
                )
        );

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openConfigKey.wasPressed()) {
                client.setScreen(
                        CoolModConfigScreen.create(client.currentScreen)
                );
            }
        });
    }
    private static KeyBinding openConfigKey;
}
