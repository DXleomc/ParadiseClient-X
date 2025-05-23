package io.github.spigotrce.paradiseclientfabric.chatroom.client;

import io.github.spigotrce.paradiseclientfabric.ParadiseClient_Fabric;
import io.github.spigotrce.paradiseclientfabric.chatroom.client.netty.ChatRoomClient;
import io.github.spigotrce.paradiseclientfabric.chatroom.common.packet.PacketRegistry;
import io.github.spigotrce.paradiseclientfabric.chatroom.common.packet.impl.DisconnectPacket;

public class Client {
    public static void connect() throws Exception {
        PacketRegistry.registerPackets();
        new ChatRoomClient(TokenStore.token.split("\\.")[3], 25565).connect();
        ParadiseClient_Fabric.CHAT_ROOM_MOD.isConnected = false;
    }

    public static void stop() {
        if (!ParadiseClient_Fabric.CHAT_ROOM_MOD.isConnected) return;
        PacketRegistry.sendPacket(
                new DisconnectPacket(),
                ParadiseClient_Fabric.CHAT_ROOM_MOD.channel
        );
        ParadiseClient_Fabric.CHAT_ROOM_MOD.channel.close();
    }
}
