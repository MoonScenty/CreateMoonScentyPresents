package me.moonscenty.createmoonscentypresents.content.sawing;

import com.simibubi.create.foundation.item.CustomUseEffectsItem;

import me.moonscenty.createmoonscentypresents.ModDataComponents;

import net.createmod.catnip.data.TriState;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;

import java.util.function.Consumer;

/**
 * Worked by hand the way Create's sandpaper is: the saw in one hand, the material in
 * the other, then hold right click. This follows {@code SandPaperItem}'s flow closely,
 * with this mod's recipe type and sounds swapped in.
 */
public class WoodenSawItem extends Item implements CustomUseEffectsItem {

    private static final int DURABILITY = 16;
    /** One per tick while sawing; the whole cut is only ~32 ticks long. */
    private static final int PARTICLES_PER_TICK = 1;
    /** A small puff when the cut completes. */
    private static final int PARTICLES_ON_FINISH = 10;

    public WoodenSawItem(Properties properties) {
        // durability() also pins the stack size to 1 - a damaged tool cannot stack.
        super(properties.stacksTo(1).durability(DURABILITY));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack saw = player.getItemInHand(hand);

        if (saw.has(ModDataComponents.SAWING.get())) {
            player.startUsingItem(hand);
            return new InteractionResultHolder<>(net.minecraft.world.InteractionResult.PASS, saw);
        }

        InteractionHand other = hand == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
        ItemStack itemInOtherHand = player.getItemInHand(other);
        if (!SawingRecipe.canSaw(level, itemInOtherHand))
            return new InteractionResultHolder<>(net.minecraft.world.InteractionResult.FAIL, saw);

        ItemStack remainder = itemInOtherHand.copy();
        ItemStack toSaw = remainder.split(1);
        player.startUsingItem(hand);
        saw.set(ModDataComponents.SAWING.get(), new SawingItemComponent(toSaw));
        player.setItemInHand(other, remainder);
        return new InteractionResultHolder<>(net.minecraft.world.InteractionResult.SUCCESS, saw);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack saw, Level level, LivingEntity entity) {
        if (!(entity instanceof Player player))
            return saw;
        if (!saw.has(ModDataComponents.SAWING.get()))
            return saw;

        ItemStack toSaw = saw.get(ModDataComponents.SAWING.get()).item();
        ItemStack result = SawingRecipe.saw(level, toSaw);

        if (level.isClientSide) {
            spawnParticles(entity.getEyePosition(1).add(entity.getLookAngle().scale(.5f)), toSaw, level,
                    PARTICLES_ON_FINISH);
            return saw;
        }

        Inventory inventory = player.getInventory();
        if (!result.isEmpty())
            inventory.placeItemBackInInventory(result);
        if (toSaw.hasCraftingRemainingItem())
            inventory.placeItemBackInInventory(toSaw.getCraftingRemainingItem());

        saw.remove(ModDataComponents.SAWING.get());
        saw.hurtAndBreak(1, entity, LivingEntity.getSlotForHand(entity.getUsedItemHand()));
        return saw;
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

    /** Released early - hand the material back rather than eating it. */
    @Override
    public void releaseUsing(ItemStack saw, Level level, LivingEntity entity, int timeLeft) {
        if (!(entity instanceof Player player))
            return;
        if (saw.has(ModDataComponents.SAWING.get())) {
            player.getInventory().placeItemBackInInventory(saw.get(ModDataComponents.SAWING.get()).item());
            saw.remove(ModDataComponents.SAWING.get());
        }
    }

    @Override
    public TriState shouldTriggerUseEffects(ItemStack stack, LivingEntity entity) {
        // Every tick, so the sawing sound can be paced by hand below.
        return TriState.TRUE;
    }

    @Override
    public boolean triggerUseEffects(ItemStack stack, LivingEntity entity, int count, RandomSource random) {
        if (stack.has(ModDataComponents.SAWING.get())) {
            ItemStack sawing = stack.get(ModDataComponents.SAWING.get()).item();
            if (!sawing.isEmpty())
                // This runs every tick, so it has to stay at a trickle.
                spawnParticles(entity.getEyePosition(1).add(entity.getLookAngle().scale(.5f)), sawing,
                        entity.level(), PARTICLES_PER_TICK);
        }

        // After 6 ticks play the sound every 7th
        if ((entity.getTicksUsingItem() - 6) % 7 == 0)
            entity.playSound(getEatingSound(), 0.9F + 0.2F * random.nextFloat(), random.nextFloat() * 0.2F + 0.9F);

        return true;
    }

    @Override
    public SoundEvent getEatingSound() {
        return SoundEvents.WOOD_HIT;
    }

    /**
     * A fallback only: {@link WoodenSawClientExtensions} draws the real stroke and
     * skips this switch. Brush is the closest vanilla motion if that ever does not run.
     * <p>
     * The particles and sound are unaffected: Create's use-effects mixin cancels the
     * vanilla path before it ever compares the animation.
     */
    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BRUSH;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 32;
    }

    /** Only reached on the client; the extension class stays off the server entirely. */
    @Override
    @OnlyIn(Dist.CLIENT)
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new WoodenSawClientExtensions());
    }
}
