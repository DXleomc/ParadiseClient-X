package io.github.spigotrce.paradiseclientfabric.command.impl;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.github.spigotrce.paradiseclientfabric.Helper;
import io.github.spigotrce.paradiseclientfabric.command.Command;
import io.github.spigotrce.paradiseclientfabric.packet.SignedVelocityPayloadPacket;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.command.CommandSource;
import net.minecraft.network.packet.c2s.common.CustomPayloadC2SPacket;

import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

public class SignedVelocityCommand extends Command {
    public SignedVelocityCommand() {
        super("signedvelocity", "Spoofs a command as another player (supports selectors)");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> root) {
        root.executes(context -> {
            Helper.printChatMessage("Usage: .signedvelocity <user|@a|@r|name1,name2> <command>");
            return SINGLE_SUCCESS;
        })
        .then(argument("user", StringArgumentType.word())
            .suggests((ctx, builder) -> {
                try {
                    String partial = ctx.getArgument("user", String.class).toLowerCase();
                    MinecraftClient client = MinecraftClient.getInstance();
                    if (client.getNetworkHandler() == null) return builder.buildFuture();
                    client.getNetworkHandler().getPlayerList().stream()
                        .map(entry -> entry.getProfile().getName())
                        .filter(name -> name.toLowerCase().startsWith(partial))
                        .forEach(builder::suggest);
                } catch (IllegalArgumentException ignored) {
                    // no-op
                }
                return builder.buildFuture();
            })
            .then(argument("command", StringArgumentType.greedyString())
                .executes(context -> {
                    String userArg = context.getArgument("user", String.class);
                    String spoofedCommand = context.getArgument("command", String.class);
                    MinecraftClient client = MinecraftClient.getInstance();

                    if (client.getNetworkHandler() == null) {
                        Helper.printChatMessage("Error: NetworkHandler is null!");
                        return SINGLE_SUCCESS;
                    }

                    List<PlayerListEntry> players = client.getNetworkHandler().getPlayerList();

                    List<PlayerListEntry> targets;

                    if (userArg.equalsIgnoreCase("@a")) {
                        targets = players;
                    } else if (userArg.equalsIgnoreCase("@r")) {
                        targets = players.isEmpty() ? List.of() : List.of(players.get(new Random().nextInt(players.size())));
                    } else if (userArg.contains(",")) {
                        List<String> targetNames = List.of(userArg.split(","));
                        targets = players.stream()
                                .filter(p -> targetNames.stream().anyMatch(n -> p.getProfile().getName().equalsIgnoreCase(n)))
                                .collect(Collectors.toList());
                    } else {
                        targets = players.stream()
                                .filter(p -> p.getProfile().getName().equalsIgnoreCase(userArg))
                                .collect(Collectors.toList());
                    }

                    if (targets.isEmpty()) {
                        Helper.printChatMessage("No matching players found for: " + userArg);
                        return SINGLE_SUCCESS;
                    }

                    for (PlayerListEntry target : targets) {
                        UUID uuid = target.getProfile().getId();
                        String name = target.getProfile().getName();
                        Helper.sendPacket(new CustomPayloadC2SPacket(
                            new SignedVelocityPayloadPacket(uuid.toString(), spoofedCommand)
                        ));
                        Helper.printChatMessage("Sent spoofed command as §b" + name + "§r [" + uuid + "]");
                    }

                    return SINGLE_SUCCESS;
                })
            )
        );
    }
}
