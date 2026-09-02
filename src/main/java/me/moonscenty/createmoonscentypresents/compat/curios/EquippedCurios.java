package me.moonscenty.createmoonscentypresents.compat.curios;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import top.theillusivec4.curios.api.CuriosApi;

/** Asking what an entity is wearing. Curios is a required dependency, so this is safe. */
public class EquippedCurios {

    /** The worn stack of this item, or empty if it is not being worn. */
    public static ItemStack find(LivingEntity entity, Item item) {
        if (entity == null)
            return ItemStack.EMPTY;
        return CuriosApi.getCuriosInventory(entity)
                .flatMap(inventory -> inventory.findFirstCurio(item))
                .map(slot -> slot.stack())
                .orElse(ItemStack.EMPTY);
    }

    public static boolean isWearing(LivingEntity entity, Item item) {
        return !find(entity, item).isEmpty();
    }
}
