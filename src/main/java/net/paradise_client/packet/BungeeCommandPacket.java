package net.paradise_client.packet;

import net.paradise_client.Helper;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.network.packet.CustomPayload;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class BungeeCommandPacket implements CustomPayload {
    public static final CustomPayload.Id<BungeeCommandPacket> ID =
            new CustomPayload.Id<>(new Identifier("atlas", "out"));

    private final String command;

    public BungeeCommandPacket(String command) {
        this.command = command;
    }

    public BungeeCommandPacket(PacketByteBuf buf) {
        this.command = buf.readString();
    }

    public String getCommand() {
        return command;
    }

    public void write(PacketByteBuf buf) {
        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            ObjectOutputStream oStream = new ObjectOutputStream(stream);
            oStream.writeUTF("commandBungee");
            oStream.writeObject(this.command);
            buf.writeBytes(stream.toByteArray());
        } catch (IOException e) {
            Helper.printChatMessage("Error writing BungeeCommandPacket: " + e.getMessage());
        }
    }

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }
}
