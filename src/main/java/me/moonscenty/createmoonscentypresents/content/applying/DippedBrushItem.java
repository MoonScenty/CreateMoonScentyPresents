package me.moonscenty.createmoonscentypresents.content.applying;

import com.simibubi.create.foundation.item.CustomUseEffectsItem;

import me.moonscenty.createmoonscentypresents.registry.ModDataComponents;

import net.createmod.catnip.data.TriState;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * A brush loaded with a substance, worked into a material by hand.
 *
 * <p>The brush is the reusable part and the dip is the consumable: one application
 * spends the load and hands back a plain {@code minecraft:brush}. Adding tar or wax
 * later needs one more dipped brush item and one more crafting recipe - no new class,
 * no new recipe type, and the substances stay ordinary materials.
 *
 * <p>{@link ApplyingRecipe} names the dipped brush in each recipe, so two substances
 * can turn the same material into different things.
 *
 * <p>The vanilla brushing animation is used as is: it already is the back-and-forth
 * motion this represents, so there is no custom hand transform here.
 */
public class DippedBrushItem extends Item implements CustomUseEffectsItem {

    /** Longer than a saw stroke - working a substance in is a patient motion. */
    private static final int USE_TICKS = 56;
    private static final int PARTICLES_PER_TICK = 1;
    private static final int PARTICLES_ON_FINISH = 8;

    public DippedBrushItem(Properties properties) {
        // One load, one application; stacking a part used brush would be meaningless.
        super(properties.stacksTo(1));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack brush = player.getItemInHand(hand);

        if (brush.has(ModDataComponents.APPLYING.get())) {
            player.startUsingItem(hand);
            return new InteractionResultHolder<>(InteractionResult.PASS, brush);
        }

        InteractionHand other = hand == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
        ItemStack itemInOtherHand = player.getItemInHand(other);
        if (!ApplyingRecipe.canApply(level, brush, itemInOtherHand))
            return new InteractionResultHolder<>(InteractionResult.FAIL, brush);

        ItemStack remainder = itemInOtherHand.copy();
        ItemStack toCoat = remainder.split(1);
        player.startUsingItem(hand);
        brush.set(ModDataComponents.APPLYING.get(), new ApplyingItemComponent(toCoat));
        player.setItemInHand(other, remainder);
        return new InteractionResultHolder<>(InteractionResult.SUCCESS, brush);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack brush, Level level, LivingEntity entity) {
        if (!(entity instanceof Player player))
            return brush;
        if (!brush.has(ModDataComponents.APPLYING.get()))
            return brush;

        ItemStack toCoat = brush.get(ModDataComponents.APPLYING.get()).item();
        // The brush is read before the component is cleared: the recipe is chosen by the
        // pair, so the lookup needs the loaded brush as well as the material.
        ItemStack result = ApplyingRecipe.apply(level, brush, toCoat);

        if (level.isClientSide) {
            spawnParticles(entity.getEyePosition(1).add(entity.getLookAngle().scale(.5f)), toCoat, level,
                    PARTICLES_ON_FINISH);
            return brush;
        }

        Inventory inventory = player.getInventory();
        if (!result.isEmpty())
            inventory.placeItemBackInInventory(result);
        if (toCoat.hasCraftingRemainingItem())
            inventory.placeItemBackInInventory(toCoat.getCraftingRemainingItem());

        // The load is spent. A fresh brush is handed back rather than the one that was
        // dipped, so any wear the original carried is forgiven - brushes are cheap and
        // tracking it through a crafting step would cost more than it is worth.
        return new ItemStack(Items.BRUSH);
    }

    public static void spawnParticles(Vec3 location, ItemStack stack, Level level, int count) {
        RandomSource random = level.random;
        for (int i = 0; i < count; i++) {
            double dx = (random.nextDouble() - 0.5) / 4;
            double dy = (random.nextDouble() - 0.5) / 4;
            double dz = (random.nextDouble() - 0.5) / 4;
            level.addParticle(new ItemParticleOption(ParticleTypes.ITEM, stack),
                    location.x, location.y, location.z, dx, dy, dz);
        }
    }

    /** Released early - hand the material back rather than eating it. The load stays. */
    @Override
    public void releaseUsing(ItemStack brush, Level level, LivingEntity entity, int timeLeft) {
        if (!(entity instanceof Player player))
            return;
        if (brush.has(ModDataComponents.APPLYING.get())) {
            player.getInventory().placeItemBackInInventory(brush.get(ModDataComponents.APPLYING.get()).item());
            brush.remove(ModDataComponents.APPLYING.get());
        }
    }

    @Override
    public TriState shouldTriggerUseEffects(ItemStack stack, LivingEntity entity) {
        return TriState.TRUE;
    }

    @Override
    public boolean triggerUseEffects(ItemStack stack, LivingEntity entity, int count, RandomSource random) {
        if (stack.has(ModDataComponents.APPLYING.get())) {
            ItemStack coating = stack.get(ModDataComponents.APPLYING.get()).item();
            if (!coating.isEmpty())
                spawnParticles(entity.getEyePosition(1).add(entity.getLookAngle().scale(.5f)), coating,
                        entity.level(), PARTICLES_PER_TICK);
        }

        // Slower than the saw's seven ticks: a rub is quieter and less busy.
        if ((entity.getTicksUsingItem() - 8) % 12 == 0)
            entity.playSound(getEatingSound(), 0.7F + 0.2F * random.nextFloat(), random.nextFloat() * 0.2F + 0.7F);

        return true;
    }

    @Override
    public SoundEvent getEatingSound() {
        return SoundEvents.HONEY_BLOCK_SLIDE;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BRUSH;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return USE_TICKS;
    }
}
