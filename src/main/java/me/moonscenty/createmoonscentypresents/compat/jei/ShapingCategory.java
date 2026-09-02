package me.moonscenty.createmoonscentypresents.compat.jei;

import me.moonscenty.createmoonscentypresents.CreateMoonScentyPresents;
import me.moonscenty.createmoonscentypresents.content.shaping.ShapingRecipe;
import me.moonscenty.createmoonscentypresents.registry.ModItems;
import me.moonscenty.createmoonscentypresents.registry.ModRecipes;

import com.mojang.blaze3d.vertex.PoseStack;

import mezz.jei.api.gui.ITickTimer;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;

/** The "work it with the chisel" recipes, shown as input -> arrow -> output. */
public class ShapingCategory implements IRecipeCategory<RecipeHolder<ShapingRecipe>> {

    public static final RecipeType<RecipeHolder<ShapingRecipe>> TYPE = RecipeType.createRecipeHolderType(
            ResourceLocation.fromNamespaceAndPath(CreateMoonScentyPresents.MODID, "shaping"));

    private static final int WIDTH = 116;
    private static final int HEIGHT = 54;

    // Slots are 18 wide. Placed symmetrically, so the gap between them - and every
    // thing drawn in it - is centred on the category.
    private static final int SLOT_Y = 32;
    private static final int INPUT_X = 8;
    private static final int OUTPUT_X = WIDTH - 8 - 18;
    private static final int GAP_CENTER = (INPUT_X + 18 + OUTPUT_X) / 2;

    private static final int ARROW_WIDTH = 24;
    private static final int ARROW_X = GAP_CENTER - ARROW_WIDTH / 2;
    private static final int ARROW_Y = SLOT_Y + 1;

    // The stock being cut, drawn larger than an inventory item so the chisel reads
    // against it.
    private static final float STOCK_SCALE = 1.5F;
    private static final int STOCK_SIZE = Math.round(16 * STOCK_SCALE);
    private static final int STOCK_X = GAP_CENTER - STOCK_SIZE / 2;
    private static final int STOCK_Y = 4;

    // The chisel stays at item size and rides on the stock's centre line.
    private static final int CHISEL_X = GAP_CENTER - 8;
    private static final int CHISEL_Y = STOCK_Y + (STOCK_SIZE - 16) / 2;
    /** Along the blade: driven in down-left, drawn back up-right. */
    private static final int JAB_TRAVEL = 5;
    private static final int JAB_TICKS = 16;
    /** Vanilla renders GUI items at z 150; this lifts the chisel clear of the stock. */
    private static final int CHISEL_DEPTH = 200;

    private final IDrawableStatic arrow;
    private final IDrawable icon;
    private final ItemStack chisel;
    private final ITickTimer jab;

    public ShapingCategory(IGuiHelper guiHelper) {
        this.arrow = guiHelper.getRecipeArrow();
        this.chisel = new ItemStack(ModItems.STONE_CHISEL.get());
        this.icon = guiHelper.createDrawableItemStack(chisel);
        // Counts 0..360 over one jab, so the value can be read straight as degrees.
        this.jab = guiHelper.createTickTimer(JAB_TICKS, 360, false);
    }

    @Override
    public RecipeType<RecipeHolder<ShapingRecipe>> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable(ModRecipes.SHAPING_CATEGORY_KEY);
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public int getWidth() {
        return WIDTH;
    }

    @Override
    public int getHeight() {
        return HEIGHT;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<ShapingRecipe> holder, IFocusGroup focuses) {
        ShapingRecipe recipe = holder.value();
        builder.addSlot(RecipeIngredientRole.INPUT, INPUT_X, SLOT_Y)
                .addIngredients(recipe.input());
        builder.addSlot(RecipeIngredientRole.OUTPUT, OUTPUT_X, SLOT_Y)
                .addItemStack(recipe.result());
    }

    @Override
    public void draw(RecipeHolder<ShapingRecipe> holder, IRecipeSlotsView slots, GuiGraphics graphics,
            double mouseX, double mouseY) {
        arrow.draw(graphics, ARROW_X, ARROW_Y);

        PoseStack pose = graphics.pose();
        ItemStack[] inputs = holder.value().input().getItems();
        if (inputs.length > 0) {
            pose.pushPose();
            pose.translate(STOCK_X, STOCK_Y, 0);
            pose.scale(STOCK_SCALE, STOCK_SCALE, 1.0F);
            graphics.renderItem(inputs[0], 0, 0);
            pose.popPose();
        }

        // A chisel is driven in quickly and eased back out, so the phase is shaped
        // rather than a plain sine: 0 fully withdrawn, 1 at the end of the cut.
        float cycle = jab.getValue() / 360.0F;
        float driven = cycle < 0.3F ? cycle / 0.3F
                : Mth.cos((cycle - 0.3F) / 0.7F * Mth.HALF_PI);

        int offset = Math.round(driven * JAB_TRAVEL);
        pose.pushPose();
        // Both items would otherwise land on the same depth and fight for the pixels.
        pose.translate(0, 0, CHISEL_DEPTH);
        graphics.renderItem(chisel, CHISEL_X - offset, CHISEL_Y + offset);
        pose.popPose();
    }
}
