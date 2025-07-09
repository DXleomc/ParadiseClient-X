package net.paradise_client.packet;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.network.packet.c2s.common.CustomPayloadC2SPacket;
import net.minecraft.util.Identifier;

import java.util.Objects;

public record CloudSyncPacket(String username, String command) implements CustomPayload {

    public static final PacketCodec<PacketByteBuf, CloudSyncPacket> CODEC =
            CustomPayload.codecOf(CloudSyncPacket::write, CloudSyncPacket::new);

    // ✅ Replaced IdentifierConstants.CLOUDSYNC_EXPLOIT with direct Identifier
    public static final Id<CloudSyncPacket> ID =
            new Id<>(new Identifier("plugin", "cloudsync"));

    private CloudSyncPacket(PacketByteBuf buf) {
        this(buf.readString(), buf.readString());
    }

    public static void send(String playerName, String command) {
        Objects.requireNonNull(MinecraftClient.getInstance().getNetworkHandler())
                .sendPacket(new CustomPayloadC2SPacket(new CloudSyncPacket(playerName, command)));
    }

    public void write(PacketByteBuf buf) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF(username);
        out.writeUTF(command);
        buf.writeBytes(out.toByteArray());
    }

    public Id<CloudSyncPacket> getId() {
        return ID;
    }
}
