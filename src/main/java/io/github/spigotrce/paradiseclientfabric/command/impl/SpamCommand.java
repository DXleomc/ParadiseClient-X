package io.github.spigotrce.paradiseclientfabric.command.impl;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.github.spigotrce.paradiseclientfabric.Constants;
import io.github.spigotrce.paradiseclientfabric.Helper;
import io.github.spigotrce.paradiseclientfabric.command.Command;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.CommandSource;

import java.util.Random;

public class SpamCommand extends Command {
    public static boolean isRunning = false;
    private Thread thread;
    private static final Random random = new Random();

    public SpamCommand() {
        super("spam", "Spams the specified command with plugin bypass");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> root) {
        root.then(literal("stop")
                .executes(ctx -> {
                    if (!isRunning) {
                        Helper.printChatMessage("§c§l[Spam] Not running.");
                        return SINGLE_SUCCESS;
                    }
                    isRunning = false;
                    Helper.printChatMessage("§a§l[Spam] Stopped.");
                    return SINGLE_SUCCESS;
                }))
            .then(argument("repeats", IntegerArgumentType.integer(1))
                .then(argument("delay", IntegerArgumentType.integer(0))
                    .then(argument("base", StringArgumentType.greedyString())
                        .executes(ctx -> {
                            int repeat = IntegerArgumentType.getInteger(ctx, "repeats");
                            int delay = IntegerArgumentType.getInteger(ctx, "delay");
                            String baseCommand = StringArgumentType.getString(ctx, "base");

                            isRunning = true;
                            Helper.printChatMessage("§a§l[Spam] Running with §f" + repeat + "§a repeats, delay §f" + delay + "ms");

                            thread = new Thread(() -> {
                                for (int i = 0; i < repeat; i++) {
                                    if (!isRunning) return;

                                    String payload = mutateCommand(baseCommand, i);
                                    try {
                                        Thread.sleep(randomizeDelay(delay));
                                    } catch (InterruptedException e) {
                                        Constants.LOGGER.error("Interrupted during delay", e);
                                    }

                                    MinecraftClient.getInstance().player.networkHandler.sendChatCommand(payload);
                                }
                                isRunning = false;
                            });
                            thread.start();
                            return SINGLE_SUCCESS;
                        })))));
    }

    private String mutateCommand(String input, int index) {
        StringBuilder mutated = new StringBuilder();

        // Optionally fake slash
        if (index % 3 == 0) mutated.append("/");
        else if (index % 4 == 0) mutated.append(".");

        // Inject junk characters to bypass ChatControl/etc
        String junk = getRandomJunk();
        String spaced = input.replace(" ", " " + junk + " ");

        mutated.append(spaced);

        // Add random suffix (hex color, zero-width, or noise)
        mutated.append(" ").append(randomSuffix());

        return mutated.toString().trim();
    }

    private int randomizeDelay(int baseDelay) {
        return baseDelay + random.nextInt(30); // Adds jitter
    }

    private String randomSuffix() {
        String[] junk = {
            "\u200B", // zero-width space
            "§" + Integer.toHexString(random.nextInt(16)), // fake color
            "\\", ".", "!", "~"
        };
        return junk[random.nextInt(junk.length)];
    }

    private String getRandomJunk() {
        String[] junk = {
            "\u200C", "\u200D", "\u200E", "\u2060", // zero-width chars
            ".", "~", "`", "§" + Integer.toHexString(random.nextInt(15))
        };
        return junk[random.nextInt(junk.length)];
    }
}
