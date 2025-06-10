package io.github.spigotrce.paradiseclientfabric.packet;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import java.util.UUID;

public record AuthMeVelocityPayloadPacket(String spoofedUsername, UUID spoofedUUID, String spoofedIP) implements CustomPayload {
    public static final PacketCodec<PacketByteBuf, AuthMeVelocityPayloadPacket> CODEC =
            CustomPayload.codecOf(AuthMeVelocityPayloadPacket::write, AuthMeVelocityPayloadPacket::read);

    public static final Id<AuthMeVelocityPayloadPacket> ID = new Id<>(Identifier.of("authmevelocity", "main"));

    // Reader
    private static AuthMeVelocityPayloadPacket read(PacketByteBuf buf) {
        String username = buf.readString(Short.MAX_VALUE);
        UUID uuid = buf.readUuid();
        String ip = buf.readString(Short.MAX_VALUE);
        return new AuthMeVelocityPayloadPacket(username, uuid, ip);
    }

    // Writer
    private void write(PacketByteBuf buf) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("LOGIN");
        out.writeUTF(spoofedUsername);
        out.writeUTF(spoofedUUID.toString());
        out.writeUTF(spoofedIP);
        buf.writeBytes(out.toByteArray());
    }

    @Override
    public CustomPayload.Id<AuthMeVelocityPayloadPacket> getId() {
        return ID;
    }
}
