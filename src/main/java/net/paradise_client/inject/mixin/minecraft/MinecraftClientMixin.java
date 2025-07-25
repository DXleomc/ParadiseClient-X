package net.paradise_client.inject.mixin.minecraft;

import net.minecraft.client.MinecraftClient;
import net.paradise_client.Constants;
import net.paradise_client.ParadiseClient;
import net.paradise_client.chatroom.client.Client;
import net.paradise_client.event.minecraft.ClientShutdownEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Mixin class to modify the behavior of the MinecraftClient class.
 * <p>
 * This class injects code into various methods of the MinecraftClient class
 * to customize the window title, handle client shutdown, start a Discord
 * RPC thread, and keep track of the currently displayed screen.
 * </p>
 *
 * @author SpigotRCE
 * @since 1.0
 */
@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {

    /**
     * Injects code to modify the window title returned by the getWindowTitle method.
     * <p>
     * This method sets the window title to include the ParadiseClient edition,
     * version, and the game version.
     * </p>
     *
     * @param callback Callback information for the return value.
     */
    @Inject(method = "getWindowTitle", at = @At(
            value = "INVOKE",
            target = "Ljava/lang/StringBuilder;append(Ljava/lang/String;)Ljava/lang/StringBuilder;",
            ordinal = 1),
            cancellable = true)
    private void getClientTitle(CallbackInfoReturnable<String> callback) {
        callback.setReturnValue(Constants.WINDOW_TITLE);
    }

    /**
     * Injects code at the start of the close method to perform additional cleanup.
     * <p>
     * This method shuts down the chat room client when the Minecraft client is closed.
     * </p>
     *
     * @param ci Callback information.
     */
    @Inject(method = "close", at = @At(value = "HEAD"))
    private void closeHead(CallbackInfo ci) {
        try {
            ParadiseClient.EVENT_MANAGER.fireEvent(new ClientShutdownEvent());
        } catch (Exception e) {
            Constants.LOGGER.error("Unable to fire ClientShutdownEvent", e);
        }
        Client.stop();
    }
}
