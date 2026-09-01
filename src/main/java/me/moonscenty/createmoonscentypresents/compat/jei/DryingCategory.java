package me.moonscenty.createmoonscentypresents.compat.jei;

import java.util.HashMap;
import java.util.Map;

import me.moonscenty.createmoonscentypresents.CreateMoonScentyPresents;
import me.moonscenty.createmoonscentypresents.content.processing.DryingRecipe;
import me.moonscenty.createmoonscentypresents.registry.ModBlocks;
import me.moonscenty.createmoonscentypresents.registry.ModRecipes;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;

/** The rack recipes, shown as input -> arrow -> output with how long the wait is. */
public class DryingCategory implements IRecipeCategory<RecipeHolder<DryingRecipe>> {

    public static final RecipeType<RecipeHolder<DryingRecipe>> TYPE = RecipeType.createRecipeHolderType(
            ResourceLocation.fromNamespaceAndPath(CreateMoonScentyPresents.MODID, "drying"));

    private static final int WIDTH = 116;
    private static final int HEIGHT = 40;

    // Slots are 18 wide. Placed symmetrically, so the gap between them - and every
    // thing drawn in it - is centred on the category.
    private static final int SLOT_Y = 6;
    private static final int INPUT_X = 8;
    private static final int OUTPUT_X = WIDTH - 8 - 18;
    private static final int GAP_CENTER = (INPUT_X + 18 + OUTPUT_X) / 2;

    private static final int ARROW_WIDTH = 24;
    private static final int ARROW_X = GAP_CENTER - ARROW_WIDTH / 2;
    private static final int ARROW_Y = SLOT_Y + 1;

    private static final int TIME_Y = SLOT_Y + 24;
    private static final int TIME_COLOUR = 0xFF808080;

    private final IGuiHelper guiHelper;
    private final IDrawable icon;

    // One arrow per distinct duration: the animation runs at the recipe's own speed,
    // and recipes that take the same time can share.
    private final Map<Integer, IDrawableAnimated> arrows = new HashMap<>();

    public DryingCategory(IGuiHelper guiHelper) {
        this.guiHelper = guiHelper;
        this.icon = guiHelper.createDrawableItemStack(new ItemStack(ModBlocks.DRYING_RACK.get()));
    }

    @Override
    public RecipeType<RecipeHolder<DryingRecipe>> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable(ModRecipes.DRYING_CATEGORY_KEY);
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
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<DryingRecipe> holder, IFocusGroup focuses) {
        DryingRecipe recipe = holder.value();
        builder.addSlot(RecipeIngredientRole.INPUT, INPUT_X, SLOT_Y)
                .addIngredients(recipe.input());
        builder.addSlot(RecipeIngredientRole.OUTPUT, OUTPUT_X, SLOT_Y)
                .addItemStack(recipe.result());
    }

    @Override
    public void draw(RecipeHolder<DryingRecipe> holder, IRecipeSlotsView slots, GuiGraphics graphics,
            double mouseX, double mouseY) {
        int ticks = holder.value().processingTime();
        arrows.computeIfAbsent(ticks, guiHelper::createAnimatedRecipeArrow)
                .draw(graphics, ARROW_X, ARROW_Y);

        Font font = Minecraft.getInstance().font;
        Component time = Component.translatable(ModRecipes.DRYING_TIME_KEY, seconds(ticks));
        graphics.drawString(font, time, GAP_CENTER - font.width(time) / 2, TIME_Y, TIME_COLOUR, false);
    }

    /** Ticks as seconds, without a trailing ".0" on the whole ones. */
    private static String seconds(int ticks) {
        float value = ticks / 20f;
        return value == Math.round(value) ? String.valueOf(Math.round(value)) : String.format("%.1f", value);
    }
}
