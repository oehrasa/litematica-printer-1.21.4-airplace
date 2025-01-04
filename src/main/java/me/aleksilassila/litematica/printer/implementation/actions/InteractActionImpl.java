package me.aleksilassila.litematica.printer.implementation.actions;

import me.aleksilassila.litematica.printer.actions.InteractAction;
import me.aleksilassila.litematica.printer.implementation.PrinterPlacementContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Direction;

public class InteractActionImpl extends InteractAction {
    private PrinterPlacementContext ctx;

    public InteractActionImpl(PrinterPlacementContext context) {
        super(context);
        this.ctx = context;
    }

    @Override
    protected void interact(MinecraftClient client, ClientPlayerEntity player, Hand hand, BlockHitResult hitResult) {
        if (client.interactionManager != null) {
            client.interactionManager.interactBlock(player, hand, hitResult);
            client.interactionManager.interactItem(player, hand);
        }
    }
}
