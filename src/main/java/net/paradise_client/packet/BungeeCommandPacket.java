package net.paradise_client.packet;

import io.github.spigotrce.paradiseclientfabric.Helper;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import net.minecraft.PacketByteBuf;
import net.minecraft.Identifier;
import net.minecraft.CustomPayload;
import net.minecraft.PacketCodec;

public record BungeeCommandPacket(String command) implements CustomPayload {
    public static final PacketCodec<PacketByteBuf, BungeeCommandPacket> CODEC = CustomPayload.codecOf(BungeeCommandPacket::write, BungeeCommandPacket::new);
    public static final CustomPayload.Id<BungeeCommandPacket> ID = new CustomPayload.Id(Identifier.of("atlas", "out"));

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
        }
        catch (IOException e) {
            Helper.printChatMessage(e.getMessage());
        }
    }

    public CustomPayload.Id<BungeeCommandWriter> getId() {
        return ID;
    }
      }
