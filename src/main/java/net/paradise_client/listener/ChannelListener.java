package net.paradise_client.listener;

import io.github.spigotrce.eventbus.event.EventHandler;
import io.github.spigotrce.eventbus.event.listener.Listener;
import net.minecraft.network.PacketByteBuf;
import net.paradise_client.Constants;
import net.paradise_client.Helper;
import net.paradise_client.ParadiseClient;
import net.paradise_client.event.channel.PluginMessageEvent;

import java.nio.charset.Charset;
import java.util.Objects;

/**
 * Listener responsible for notifying when a plugin message
 * is sent from the client
 */
public class ChannelListener implements Listener {
    @SuppressWarnings("unused")
    @EventHandler
    public void onChannelRegister(PluginMessageEvent event) {
        String channelName = event.getChannel();
        PacketByteBuf buf = event.getBuf();
        try {
            if (Objects.equals(channelName, "minecraft:register") || Objects.equals(channelName, "REGISTER")) // 1.13 channel or 1.8 channel
                for (String splitted : buf.toString(Charset.defaultCharset()).split("\000")) {
                    Helper.printChatMessage("&fChannel: &" + (ParadiseClient.NETWORK_MOD.getRegisteredChannelsByName().contains(splitted) ? "c " : "d ") + splitted);
                    if (ParadiseClient.NETWORK_MOD.getRegisteredChannelsByName().contains(splitted))
                        Helper.showNotification("Exploit found!", splitted);
                }
            else
                Helper.printChatMessage("&fChannel: &d" + channelName + " &fData: &d" + buf.toString(Charset.defaultCharset()));
        } catch (Exception e) {
            Helper.printChatMessage("&4Error handling listener for payload for channel: " + channelName + " " + e.getMessage());
            Constants.LOGGER.error("&4Error handling listener for channel: {} {}", channelName, e);
        }
    }
}
