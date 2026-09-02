package me.moonscenty.createmoonscentypresents.mixin;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.foundation.utility.CreateLang;

import me.moonscenty.createmoonscentypresents.content.kinetics.ModKineticLimits;
import me.moonscenty.createmoonscentypresents.registry.ModLang;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntity;

/**
 * Adds the age's speed ceiling to what goggles say about a part.
 *
 * <p>A player looking at a primitive shaft needs to know two numbers, not one: how fast
 * it is turning and how fast it may turn. Create shows the first and has no notion of
 * the second, and the parts that have a ceiling are spread across several block entity
 * classes - most of them Create's own - so this goes in at the root rather than being
 * written into each.
 */
@Mixin(KineticBlockEntity.class)
public class KineticBlockEntityMixin {

    @Inject(method = "addToGoggleTooltip", at = @At("RETURN"))
    private void createmoonscentypresents$addSpeedLimit(List<Component> tooltip, boolean isPlayerSneaking,
            CallbackInfoReturnable<Boolean> info) {
        if (!info.getReturnValue())
            return;

        BlockEntity blockEntity = (BlockEntity) (Object) this;
        int limit = ModKineticLimits.of(blockEntity.getBlockState());
        if (limit == ModKineticLimits.UNLIMITED)
            return;

        float speed = Math.abs(((KineticBlockEntity) (Object) this).getSpeed());
        // Red once it is over: the part is about to break, and that is the whole reason
        // the number is here.
        ChatFormatting colour = speed > limit ? ChatFormatting.RED : ChatFormatting.GRAY;
        CreateLang.translate(ModLang.SPEED_LIMIT_KEY, limit)
                .style(colour)
                .forGoggles(tooltip, 1);
    }
}
