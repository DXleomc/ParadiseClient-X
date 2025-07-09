package net.paradise_client;

import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

/**
 * Utility class for parsing legacy color codes (&a, &c, etc.)
 */
public class CC {

    /**
     * Converts '&'-style color codes into Minecraft's '§' style and returns a text component.
     *
     * @param message The message with legacy color codes.
     * @return A MutableText object with color formatting.
     */
    public static MutableText parseColorCodes(String message) {
        String formatted = message
                .replace("&0", "§0").replace("&1", "§1").replace("&2", "§2")
                .replace("&3", "§3").replace("&4", "§4").replace("&5", "§5")
                .replace("&6", "§6").replace("&7", "§7").replace("&8", "§8")
                .replace("&9", "§9").replace("&a", "§a").replace("&b", "§b")
                .replace("&c", "§c").replace("&d", "§d").replace("&e", "§e")
                .replace("&f", "§f").replace("&l", "§l").replace("&n", "§n")
                .replace("&m", "§m").replace("&o", "§o").replace("&r", "§r");

        return Text.literal(formatted);
    }
}
