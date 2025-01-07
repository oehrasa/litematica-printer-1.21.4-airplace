package me.aleksilassila.litematica.printer;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import me.aleksilassila.litematica.printer.actions.Action;
import me.aleksilassila.litematica.printer.actions.PrepareAction;
import me.aleksilassila.litematica.printer.config.Configs;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.Item;

public class ActionHandler {
    private final MinecraftClient client;
    private final ClientPlayerEntity player;
    private final Queue<Action> actionQueue = new LinkedList<>();
    public PrepareAction lookAction = null;

    public ActionHandler(MinecraftClient client, ClientPlayerEntity player) {
        this.client = client;
        this.player = player;
    }

    private int tick = 0;

    public void onGameTick() {
        int tickRate = Configs.PRINTING_INTERVAL.getIntegerValue();
        tick = tick % tickRate == tickRate - 1 && tick > 0 ? 0 : tick + 1;

        if (tick % tickRate != 0 || tick < 0)
            return;
        if (lookAction != null)
            lookAction.send(client, player);

        Action nextAction = actionQueue.poll();

        if (nextAction != null) {
            Printer.printDebug("Sending action {}", nextAction);
            if (nextAction instanceof PrepareAction prep_action) {
                Item required_item = prep_action.context.getStack().getItem();
                Item current_item_stack = player.getInventory().getStack(player.getInventory().selectedSlot).getItem();
                if (!required_item.equals(current_item_stack)) {
                    lookAction = prep_action;
                    tick = -Configs.SWITCH_INTERVAL.getIntegerValue();
                }
                prep_action.send(client, player);
            }
            nextAction.send(client, player);
        } else {
            lookAction = null;
        }
    }

    public boolean acceptsActions() {
        return actionQueue.isEmpty();
    }

    public void addActions(Action... actions) {
        if (!acceptsActions()) {
            return;
        }

        for (Action action : actions) {
            if (action instanceof PrepareAction) {
                lookAction = (PrepareAction) action;
            }
        }

        actionQueue.addAll(List.of(actions));
    }
}
