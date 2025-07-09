package net.paradise_client.packet;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.network.packet.PacketCodec;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public record BungeeCommandPacket(String command) implements CustomPayload {
    public static final PacketCodec<PacketByteBuf, BungeeCommandPacket> CODEC =
        CustomPayload.codecOf(BungeeCommandPacket::write, BungeeCommandPacket::new);

    public static final CustomPayload.Id<BungeeCommandPacket> ID =
        new CustomPayload.Id<>(new Identifier("atlas", "out"));

    private BungeeCommandPacket(PacketByteBuf buf) {
        this(buf.readString());
    }

    public void write(PacketByteBuf buf) {
        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            ObjectOutputStream oStream = new ObjectOutputStream(stream);
            oStream.writeUTF("commandBungee");
            oStream.writeObject(this.command);
            buf.writeBytes(stream.toByteArray());
        } catch (IOException e) {
            System.err.println("[BungeeCommandPacket] Failed to write: " + e.getMessage());
        }
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
