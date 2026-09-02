package me.moonscenty.createmoonscentypresents.compat.jei;

import java.util.HashMap;
import java.util.Map;

import me.moonscenty.createmoonscentypresents.content.casting.CastingRecipe;
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

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.fluids.crafting.SizedFluidIngredient;

/**
 * Molten metal, the mould it is poured over, and what comes out.
 *
 * <p>The mould has a slot of its own rather than being folded into the inputs, because
 * it is the half of the recipe that decides the shape - and usually it is still there
 * afterwards.
 */
public class CastingCategory implements IRecipeCategory<RecipeHolder<CastingRecipe>> {

    public static final RecipeType<RecipeHolder<CastingRecipe>> TYPE = RecipeType.createRecipeHolderType(
            ResourceLocation.fromNamespaceAndPath("createmoonscentypresents", "casting"));

    private static final int WIDTH = 140;
    private static final int HEIGHT = 50;

    private static final int SLOT_Y = 6;
    private static final int FLUID_X = 8;
    private static final int MOLD_X = FLUID_X + 20;
    private static final int OUTPUT_X = WIDTH - 8 - 18;
    private static final int GAP_CENTER = (MOLD_X + 18 + OUTPUT_X) / 2;

    private static final int ARROW_WIDTH = 24;
    private static final int ARROW_X = GAP_CENTER - ARROW_WIDTH / 2;
    private static final int ARROW_Y = SLOT_Y + 1;

    private static final int TIME_Y = SLOT_Y + 24;
    private static final int MOLD_Y = TIME_Y + 10;
    private static final int TEXT_COLOUR = 0xFF808080;

    private static final int TANK_CAPACITY = 1000;

    private final IGuiHelper guiHelper;
    private final IDrawable icon;
    private final Map<Integer, IDrawableAnimated> arrows = new HashMap<>();

    public CastingCategory(IGuiHelper guiHelper) {
        this.guiHelper = guiHelper;
        this.icon = guiHelper.createDrawableItemStack(new ItemStack(ModBlocks.CASTING_TABLE.get()));
    }

    @Override
    public RecipeType<RecipeHolder<CastingRecipe>> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable(ModRecipes.CASTING_CATEGORY_KEY);
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
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<CastingRecipe> holder,
            IFocusGroup focuses) {
        CastingRecipe recipe = holder.value();

        SizedFluidIngredient fluid = recipe.fluid();
        builder.addSlot(RecipeIngredientRole.INPUT, FLUID_X, SLOT_Y)
                .setFluidRenderer(TANK_CAPACITY, false, 16, 16)
                .addFluidStack(fluid.getFluids()[0].getFluid(), fluid.amount());

        // Catalyst rather than input when it survives: JEI then shows it as something
        // you need rather than something you spend.
        builder.addSlot(recipe.moldConsumed() ? RecipeIngredientRole.INPUT : RecipeIngredientRole.CATALYST,
                MOLD_X, SLOT_Y).addIngredients(recipe.mold());

        builder.addSlot(RecipeIngredientRole.OUTPUT, OUTPUT_X, SLOT_Y).addItemStack(recipe.result());
    }

    @Override
    public void draw(RecipeHolder<CastingRecipe> holder, IRecipeSlotsView slots, GuiGraphics graphics,
            double mouseX, double mouseY) {
        CastingRecipe recipe = holder.value();
        int ticks = recipe.processingTime();
        arrows.computeIfAbsent(ticks, guiHelper::createAnimatedRecipeArrow).draw(graphics, ARROW_X, ARROW_Y);

        Font font = Minecraft.getInstance().font;
        Component time = Component.translatable(ModRecipes.TIME_KEY, JeiFormat.seconds(ticks));
        graphics.drawString(font, time, GAP_CENTER - font.width(time) / 2, TIME_Y, TEXT_COLOUR, false);

        Component mold = Component.translatable(
                recipe.moldConsumed() ? ModRecipes.MOLD_CONSUMED_KEY : ModRecipes.MOLD_KEPT_KEY)
                .withStyle(ChatFormatting.GRAY);
        graphics.drawString(font, mold, GAP_CENTER - font.width(mold) / 2, MOLD_Y, TEXT_COLOUR, false);
    }
}
