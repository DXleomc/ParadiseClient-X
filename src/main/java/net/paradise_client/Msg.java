package net.paradise_client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.MutableText;
import net.minecraft.util.Formatting;

import java.net.URI;
import java.net.URISyntaxException;

public class Msg {

    public enum ClickAction {
        RUN_COMMAND,
        OPEN_URL,
        SUGGEST_COMMAND,
        COPY_TO_CLIPBOARD
    }

    public static void sendFormattedMessage(String message, boolean bossBarOverlay,
                                            ClickAction action, String actionValue) {
        if (MinecraftClient.getInstance().player == null) return;
        MutableText textComponent = CC.parseColorCodes(message);

        if (action != null && actionValue != null) {
            try {
                ClickEvent clickEvent = createClickEvent(action, actionValue);
                textComponent.setStyle(textComponent.getStyle()
                        .withClickEvent(clickEvent)
                        .withFormatting(Formatting.UNDERLINE));
            } catch (URISyntaxException e) {
                throw new IllegalArgumentException("Invalid URL format for OPEN_URL action", e);
            }
        }

        MinecraftClient.getInstance().player.sendMessage(textComponent, bossBarOverlay);
    }

    public static void sendFormattedMessage(String message, boolean bossBarOverlay) {
        sendFormattedMessage(message, bossBarOverlay, null, null);
    }

    public static void sendFormattedMessage(String message) {
        sendFormattedMessage(message, false);
    }

    private static ClickEvent createClickEvent(ClickAction action, String value) throws URISyntaxException {
        return switch (action) {
            case RUN_COMMAND -> new ClickEvent.RunCommand(value);
            case OPEN_URL -> new ClickEvent.OpenUrl(new URI(value));
            case SUGGEST_COMMAND -> new ClickEvent.SuggestCommand(value);
            case COPY_TO_CLIPBOARD -> new ClickEvent.CopyToClipboard(value);
        };
    }
}
