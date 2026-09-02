package me.moonscenty.createmoonscentypresents.compat.jei;

import java.util.HashMap;
import java.util.Map;

import me.moonscenty.createmoonscentypresents.CreateMoonScentyPresents;
import me.moonscenty.createmoonscentypresents.content.tapping.TapperBlockEntity;
import me.moonscenty.createmoonscentypresents.content.tapping.TappingRecipe;
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
import net.neoforged.neoforge.fluids.FluidStack;

/** What a tapper draws out of a holed log, and how long one draw takes. */
public class TappingCategory implements IRecipeCategory<RecipeHolder<TappingRecipe>> {

    public static final RecipeType<RecipeHolder<TappingRecipe>> TYPE = RecipeType.createRecipeHolderType(
            ResourceLocation.fromNamespaceAndPath(CreateMoonScentyPresents.MODID, "tapping"));

    private static final int WIDTH = 116;
    private static final int HEIGHT = 40;

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
    private final Map<Integer, IDrawableAnimated> arrows = new HashMap<>();

    public TappingCategory(IGuiHelper guiHelper) {
        this.guiHelper = guiHelper;
        this.icon = guiHelper.createDrawableItemStack(new ItemStack(ModBlocks.TAPPER.get()));
    }

    @Override
    public RecipeType<RecipeHolder<TappingRecipe>> getRecipeType() {
        return TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable(ModRecipes.TAPPING_CATEGORY_KEY);
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
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<TappingRecipe> holder, IFocusGroup focuses) {
        TappingRecipe recipe = holder.value();
        builder.addSlot(RecipeIngredientRole.INPUT, INPUT_X, SLOT_Y)
                .addIngredients(recipe.log());
        FluidStack drawn = recipe.result();
        builder.addSlot(RecipeIngredientRole.OUTPUT, OUTPUT_X, SLOT_Y)
                // Drawn against the tank it fills, so one draw reads as the fraction of
                // a tapper it is rather than as a bare number.
                .setFluidRenderer(TapperBlockEntity.TANK_CAPACITY, false, 16, 16)
                .addFluidStack(drawn.getFluid(), drawn.getAmount(), drawn.getComponentsPatch());
    }

    @Override
    public void draw(RecipeHolder<TappingRecipe> holder, IRecipeSlotsView slots, GuiGraphics graphics,
            double mouseX, double mouseY) {
        int ticks = holder.value().processingTime();
        arrows.computeIfAbsent(ticks, guiHelper::createAnimatedRecipeArrow)
                .draw(graphics, ARROW_X, ARROW_Y);

        Font font = Minecraft.getInstance().font;
        Component time = Component.translatable(ModRecipes.TIME_KEY, JeiFormat.seconds(ticks));
        graphics.drawString(font, time, GAP_CENTER - font.width(time) / 2, TIME_Y, TIME_COLOUR, false);
    }
}
