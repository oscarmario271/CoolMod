package bosha.coolmod.client;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import net.minecraft.util.Formatting;
import net.minecraft.text.MutableText;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class CoolModConfigScreen {

    public static Screen create(Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(parent)
                .setTitle(Text.literal("Cool Mod!!!"));

        // ✅ Friend Marker category
        ConfigCategory friendMarker = builder.getOrCreateCategory(
                Text.literal("Friend Marker")
        );

        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        friendMarker.addEntry(
                entryBuilder.startBooleanToggle(
                                Text.literal("Friend Marker Enabled"),
                                CoolModConfig.friendMarkerEnabled
                        )
                        .setSaveConsumer(newValue -> {
                            CoolModConfig.friendMarkerEnabled = newValue;
                            CoolModConfig.save();
                        })
                        .build()
        );

        friendMarker.addEntry(
                entryBuilder.startIntSlider(
                                Text.literal("Refresh Rate (ticks)"),
                                CoolModConfig.refreshTicks,
                                1,
                                100
                        )
                        .setSaveConsumer(value -> {
                            CoolModConfig.refreshTicks = value;
                            CoolModConfig.save();
                        })
                        .build()
        );

        friendMarker.addEntry(
                entryBuilder.startIntSlider(
                                Text.literal("Pillar Radius"),
                                CoolModConfig.pillarRadius,
                                1,
                                3
                        )
                        .setSaveConsumer(value -> {
                            CoolModConfig.pillarRadius = value;
                            CoolModConfig.save();
                        })
                        .build()
        );

        friendMarker.addEntry(
                entryBuilder.startIntSlider(
                                Text.literal("Min Distance"),
                                CoolModConfig.minDistance,
                                8,
                                128
                        )
                        .setSaveConsumer(value -> {
                            CoolModConfig.minDistance = value;
                            CoolModConfig.save();
                        })
                        .build()
        );

        friendMarker.addEntry(
                entryBuilder.startIntSlider(
                                Text.literal("Height Above Head"),
                                CoolModConfig.heightAboveHead,
                                0,
                                5
                        )
                        .setSaveConsumer(value -> {
                            CoolModConfig.heightAboveHead = value;
                            CoolModConfig.save();
                        })
                        .build()
        );

        friendMarker.addEntry(
                entryBuilder.startIntSlider(
                                Text.literal("Pillar Height"),
                                CoolModConfig.pillarHeight,
                                2,
                                40
                        )
                        .setSaveConsumer(value -> {
                            CoolModConfig.pillarHeight = value;
                            CoolModConfig.save();
                        })
                        .build()
        );

        friendMarker.addEntry(
                entryBuilder.startIntSlider(
                                Text.literal("Particle Density"),
                                CoolModConfig.particleCount,
                                20,
                                400
                        )
                        .setSaveConsumer(value -> {
                            CoolModConfig.particleCount = value;
                            CoolModConfig.save();
                        })
                        .build()
        );

        // ✅ TP Ways category
        ConfigCategory tpWays = builder.getOrCreateCategory(
                Text.literal("TP Ways")
        );

        var all = WaypointStorage.getAll();

        for (String worldKey : all.keySet()) {

            // Example key:
            // singleplayer_Waypoints_minecraft:overworld
            String[] parts = worldKey.split("_");

            String modeRaw = parts.length > 0 ? parts[0] : "unknown";
            String dimensionRaw = parts.length > 2 ? parts[2] : worldKey;

            String mode =
                    modeRaw.equalsIgnoreCase("singleplayer")
                            ? "Singleplayer"
                            : "Multiplayer";

            String dimensionId = dimensionRaw.replace("minecraft:", "").toLowerCase();

            var worldWays = all.get(worldKey);

            for (String name : worldWays.keySet()) {

                var wp = worldWays.get(name);

                String coords = String.format(
                        "(%.0f, %.0f, %.0f)",
                        wp.x,
                        wp.y,
                        wp.z
                );

                String dimensionName;
                Formatting dimensionColor;

// Capitalize + color
                switch (dimensionId) {
                    case "overworld" -> {
                        dimensionName = "Overworld";
                        dimensionColor = Formatting.GREEN;
                    }
                    case "the_nether", "nether" -> {
                        dimensionName = "Nether";
                        dimensionColor = Formatting.RED;
                    }
                    case "the_end", "end" -> {
                        dimensionName = "End";
                        dimensionColor = Formatting.LIGHT_PURPLE;
                    }
                    default -> {
                        // Fallback for modded dimensions
                        dimensionName =
                                dimensionId.substring(0, 1).toUpperCase()
                                        + dimensionId.substring(1);
                        dimensionColor = Formatting.GRAY;
                    }
                }

// Build colored text
                MutableText labelText = Text.literal(name + " | ")
                        .append(Text.literal(dimensionName).formatted(dimensionColor))
                        .append(Text.literal(" | " + mode + " " + coords));


                // Info label
                tpWays.addEntry(
                        entryBuilder.startTextDescription(labelText)
                                .build()
                );

                // Remove toggle
                tpWays.addEntry(
                        entryBuilder.startBooleanToggle(
                                        Text.literal("Remove " + name),
                                        false
                                )
                                .setSaveConsumer(value -> {
                                    if (value) {
                                        WaypointStorage.remove(worldKey, name);
                                    }
                                })
                                .build()
                );
            }
        }
        return builder.build();
    }
}
