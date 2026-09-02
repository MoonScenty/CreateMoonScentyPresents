package me.moonscenty.createmoonscentypresents.compat.curios;

import me.moonscenty.createmoonscentypresents.registry.ModItems;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import top.theillusivec4.curios.api.type.capability.ICurio;

/**
 * What the apprentice goggles do while worn: nothing of their own.
 *
 * <p>Create already draws everything a pair of goggles is for; all this has to do is
 * make Create agree that a pair is being worn. That is registered once at startup
 * through {@link net.minecraft.world.item.Item} - see {@link ModCurios} - rather than
 * ticked here, because the answer is only ever asked for, never acted on.
 */
public record ApprenticeGogglesCurio(ItemStack stack) implements ICurio {

    @Override
    public ItemStack getStack() {
        return stack;
    }

    /** Whether this player is wearing a pair. Handed to Create's own check. */
    public static boolean isWorn(Player player) {
        return EquippedCurios.isWearing(player, ModItems.APPRENTICE_GOGGLES.get());
    }
}
