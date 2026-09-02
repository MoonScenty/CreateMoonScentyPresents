package me.moonscenty.createmoonscentypresents.compat.curios;

import java.util.List;

import me.moonscenty.createmoonscentypresents.registry.ModDataComponents;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;

import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.type.capability.ICurio;

/**
 * What the satchel does while it is worn: pulls loose items in from further than an arm
 * can reach.
 *
 * <p>The reach is the whole of it - it does not hold anything, and it does not change
 * what the items are. The stone age's repeated work is picking things up off the
 * ground, and this is the reward that takes that step away.
 *
 * <p>Only reachable through the Curios capability, so carrying one in the inventory or
 * in hand does nothing.
 */
public record GatherersSatchelCurio(ItemStack stack) implements ICurio {

    /** Blocks from the wearer. Vanilla reaches about one. */
    private static final double REACH = 4.0;
    /** A whole box scan every tick is waste; five times a second is not felt. */
    private static final int SCAN_INTERVAL = 4;

    @Override
    public ItemStack getStack() {
        return stack;
    }

    @Override
    public void curioTick(SlotContext context) {
        LivingEntity wearer = context.entity();
        if (wearer == null || wearer.level().isClientSide)
            return;
        if (!(wearer instanceof Player player))
            return;
        if (!stack.getOrDefault(ModDataComponents.SATCHEL_ACTIVE.get(), true))
            return;
        if (player.tickCount % SCAN_INTERVAL != 0)
            return;

        AABB range = player.getBoundingBox().inflate(REACH);
        List<ItemEntity> loose = player.level().getEntitiesOfClass(ItemEntity.class, range,
                item -> item.isAlive() && !item.hasPickUpDelay());
        // playerTouch is the vanilla pickup: it honours the pickup delay, merges into
        // the inventory and leaves behind whatever does not fit.
        for (ItemEntity item : loose)
            item.playerTouch(player);
    }
}
