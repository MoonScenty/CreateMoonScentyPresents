package me.moonscenty.createmoonscentypresents.content.hammering;

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
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;

import java.util.function.Consumer;

/**
 * Worked by hand the way Create's sandpaper is: the hammer in one hand, the material in
 * the other, then hold right click.
 *
 * <p>The same flow as {@code WoodenSawItem}, with hammering's recipe type, sounds and
 * pacing. Kept as its own class rather than sharing a base: the two differ in every
 * constant, and one shared class would need the recipe type and component threaded
 * through it for no gain.
 */
public class StoneHammerItem extends Item implements CustomUseEffectsItem {

    /** Stone survives more work than the saw's blade, but not by much. */
    private static final int DURABILITY = 32;
    /** Longer than a saw stroke - a hammer blow is a heavier motion. */
    private static final int USE_TICKS = 40;
    /** Ticks between blows; the sound and the shake are paced off this. */
    private static final int TICKS_PER_BLOW = 10;

    private static final int PARTICLES_PER_BLOW = 4;
    private static final int PARTICLES_ON_FINISH = 12;

    public StoneHammerItem(Properties properties) {
        // durability() also pins the stack size to 1 - a damaged tool cannot stack.
        super(properties.stacksTo(1).durability(DURABILITY));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack hammer = player.getItemInHand(hand);

        if (hammer.has(ModDataComponents.HAMMERING.get())) {
            player.startUsingItem(hand);
            return new InteractionResultHolder<>(InteractionResult.PASS, hammer);
        }

        InteractionHand other = hand == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
        ItemStack itemInOtherHand = player.getItemInHand(other);
        if (!HammeringRecipe.canHammer(level, itemInOtherHand))
            return new InteractionResultHolder<>(InteractionResult.FAIL, hammer);

        ItemStack remainder = itemInOtherHand.copy();
        ItemStack toHammer = remainder.split(1);
        player.startUsingItem(hand);
        hammer.set(ModDataComponents.HAMMERING.get(), new HammeringItemComponent(toHammer));
        player.setItemInHand(other, remainder);
        return new InteractionResultHolder<>(InteractionResult.SUCCESS, hammer);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack hammer, Level level, LivingEntity entity) {
        if (!(entity instanceof Player player))
            return hammer;
        if (!hammer.has(ModDataComponents.HAMMERING.get()))
            return hammer;

        ItemStack toHammer = hammer.get(ModDataComponents.HAMMERING.get()).item();
        ItemStack result = HammeringRecipe.hammer(level, toHammer);

        if (level.isClientSide) {
            spawnParticles(entity.getEyePosition(1).add(entity.getLookAngle().scale(.5f)), toHammer, level,
                    PARTICLES_ON_FINISH);
            return hammer;
        }

        Inventory inventory = player.getInventory();
        if (!result.isEmpty())
            inventory.placeItemBackInInventory(result);
        if (toHammer.hasCraftingRemainingItem())
            inventory.placeItemBackInInventory(toHammer.getCraftingRemainingItem());

        hammer.remove(ModDataComponents.HAMMERING.get());
        hammer.hurtAndBreak(1, entity, LivingEntity.getSlotForHand(entity.getUsedItemHand()));
        return hammer;
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
    public void releaseUsing(ItemStack hammer, Level level, LivingEntity entity, int timeLeft) {
        if (!(entity instanceof Player player))
            return;
        if (hammer.has(ModDataComponents.HAMMERING.get())) {
            player.getInventory().placeItemBackInInventory(hammer.get(ModDataComponents.HAMMERING.get()).item());
            hammer.remove(ModDataComponents.HAMMERING.get());
        }
    }

    @Override
    public TriState shouldTriggerUseEffects(ItemStack stack, LivingEntity entity) {
        // Every tick, so the blows below can be paced by hand.
        return TriState.TRUE;
    }

    @Override
    public boolean triggerUseEffects(ItemStack stack, LivingEntity entity, int count, RandomSource random) {
        // Unlike sawing, which is a continuous stroke, hammering lands in discrete
        // blows - so the particles and the sound fire together on impact rather than
        // trickling every tick.
        if (entity.getTicksUsingItem() % TICKS_PER_BLOW != 0)
            return true;

        if (stack.has(ModDataComponents.HAMMERING.get())) {
            ItemStack hammering = stack.get(ModDataComponents.HAMMERING.get()).item();
            if (!hammering.isEmpty())
                spawnParticles(entity.getEyePosition(1).add(entity.getLookAngle().scale(.5f)), hammering,
                        entity.level(), PARTICLES_PER_BLOW);
        }

        entity.playSound(getEatingSound(), 0.9F + 0.2F * random.nextFloat(), random.nextFloat() * 0.2F + 0.8F);
        return true;
    }

    @Override
    public SoundEvent getEatingSound() {
        return SoundEvents.STONE_HIT;
    }

    /**
     * A fallback only: {@link StoneHammerClientExtensions} draws the real swing and
     * skips this switch.
     */
    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BRUSH;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return USE_TICKS;
    }

    /** Only reached on the client; the extension class stays off the server entirely. */
    @Override
    @OnlyIn(Dist.CLIENT)
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new StoneHammerClientExtensions());
    }
}
