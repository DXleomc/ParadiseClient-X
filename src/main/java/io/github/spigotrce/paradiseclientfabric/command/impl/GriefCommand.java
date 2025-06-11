package io.github.spigotrce.paradiseclientfabric.command.impl;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.github.spigotrce.paradiseclientfabric.Helper;
import io.github.spigotrce.paradiseclientfabric.command.Command;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.command.CommandSource;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GriefCommand extends Command {

    private final ExecutorService executor = Executors.newCachedThreadPool();

    public GriefCommand() {
        super("grief", "Multiple grief commands and structures");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> root) {
        root.then(literal("tpall")
                .executes(ctx -> {
                    ClientPlayNetworkHandler h = getHandler();
                    h.sendChatCommand("tpall");
                    h.sendChatCommand("etpall");
                    h.sendChatCommand("minecraft:tp @a @p");
                    h.sendChatCommand("tp @a @p");
                    h.sendChatCommand("minecraft:teleport @a @p");
                    return SINGLE_SUCCESS;
                })
        ).then(literal("clearinv")
                .executes(ctx -> {
                    ClientPlayNetworkHandler h = getHandler();
                    h.sendChatCommand("clear @a");
                    h.sendChatCommand("minecraft:clear @a");
                    h.sendChatCommand("inventory clear @a");
                    return SINGLE_SUCCESS;
                })
        ).then(literal("fill")
                .then(literal("air")
                        .executes(ctx -> {
                            getHandler().sendChatCommand("fill ~12 ~12 ~12 ~-12 ~-12 ~-12 air");
                            return SINGLE_SUCCESS;
                        })
                )
                .then(literal("lava")
                        .executes(ctx -> {
                            getHandler().sendChatCommand("fill ~12 ~12 ~12 ~-12 ~-12 ~-12 lava");
                            return SINGLE_SUCCESS;
                        })
                )
                .executes(ctx -> {
                    Helper.printChatMessage("§cUsage: grief fill <air/lava>");
                    return SINGLE_SUCCESS;
                })
        ).then(literal("spamfill")
                .executes(ctx -> {
                    executor.submit(() -> {
                        try {
                            ClientPlayNetworkHandler h = getHandler();
                            for (int i = 0; i < 50; i++) {
                                h.sendChatCommand("fill ~10 ~10 ~10 ~-10 ~-10 ~-10 lava");
                                Thread.sleep(100);
                            }
                        } catch (InterruptedException ignored) {}
                    });
                    return SINGLE_SUCCESS;
                })
        ).then(literal("explode")
                .executes(ctx -> {
                    ClientPlayNetworkHandler h = getHandler();
                    h.sendChatCommand("//sphere tnt 5");
                    h.sendChatCommand("//set tnt");
                    h.sendChatCommand("/summon tnt ~ ~ ~");
                    h.sendChatCommand("/summon minecraft:tnt ~ ~ ~ {Fuse:1}");
                    return SINGLE_SUCCESS;
                })
        ).then(literal("sphere")
                .then(literal("air")
                        .executes(ctx -> {
                            getHandler().sendChatCommand("/sphere air 10");
                            return SINGLE_SUCCESS;
                        })
                )
                .then(literal("lava")
                        .executes(ctx -> {
                            getHandler().sendChatCommand("/sphere lava 10");
                            return SINGLE_SUCCESS;
                        })
                )
                .executes(ctx -> {
                    Helper.printChatMessage("§cUsage: grief sphere <air/lava>");
                    return SINGLE_SUCCESS;
                })
        ).executes(ctx -> {
            Helper.printChatMessage("§cUsage: grief <tpall/fill/sphere/clearinv/explode/spamfill>");
            return SINGLE_SUCCESS;
        });
    }

    private ClientPlayNetworkHandler getHandler() {
        return Objects.requireNonNull(MinecraftClient.getInstance().getNetworkHandler());
    }
}
