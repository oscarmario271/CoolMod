package bosha.coolmod.client;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.particle.ParticleTypes;

public class FriendMarker {

    private static int tickCounter = 0;

    public static void init() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null || client.world == null) return;
            if (!CoolModConfig.friendMarkerEnabled) return;

            tickCounter++;
            if (tickCounter < CoolModConfig.refreshTicks) return;
            tickCounter = 0;

            for (AbstractClientPlayerEntity other : client.world.getPlayers()) {
                if (other == client.player) continue;

                if (other.isSneaking()) continue;

                double dx = client.player.getX() - other.getX();
                double dy = client.player.getY() - other.getY();
                double dz = client.player.getZ() - other.getZ();
                double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);

                if (distance > CoolModConfig.minDistance) continue;

                if (!FriendStorage.isFriend(other.getName().getString())) continue;

                spawnMarker(client, other);
            }
        });
    }

    private static void spawnMarker(MinecraftClient client, AbstractClientPlayerEntity player) {
        double baseX = player.getX();
        double baseY = player.getY() + player.getHeight() + CoolModConfig.heightAboveHead;
        double baseZ = player.getZ();

        for (int i = 0; i < CoolModConfig.particleCount; i++) {
            double height = Math.random() * CoolModConfig.pillarHeight;
            double radius = Math.random() * CoolModConfig.pillarRadius;

            double angle = Math.random() * Math.PI * 2;

            double offsetX = Math.cos(angle) * radius;
            double offsetZ = Math.sin(angle) * radius;

            client.particleManager.addParticle(
                    ParticleTypes.END_ROD,
                    baseX + offsetX,
                    baseY + height,
                    baseZ + offsetZ,
                    0.0,
                    0.005,   // slow upward drift
                    0.0
            );
        }
    }
}
