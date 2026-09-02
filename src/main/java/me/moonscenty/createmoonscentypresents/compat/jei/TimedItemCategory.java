package me.moonscenty.createmoonscentypresents.compat.jei;

import java.util.HashMap;
import java.util.Map;

import me.moonscenty.createmoonscentypresents.content.heat.HeatLevel;
import me.moonscenty.createmoonscentypresents.content.processing.TimedItemRecipe;
import me.moonscenty.createmoonscentypresents.registry.ModRecipes;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SingleRecipeInput;

/**
 * One page for every station that only waits: a rack, a kiln, a charcoal pit.
 *
 * <p>They differ in what they are and what fire they need, not in what the page has to
 * say - one thing in, one thing out, and how long. The differences are handed in rather
 * than written out three times.
 *
 * @param <R> the recipe this page lists
 */
public class TimedItemCategory<R extends Recipe<SingleRecipeInput> & TimedItemRecipe>
        implements IRecipeCategory<RecipeHolder<R>> {

    private static final int WIDTH = 116;
    private static final int HEIGHT = 50;

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

    private final RecipeType<RecipeHolder<R>> type;
    private final String titleKey;
    private final IGuiHelper guiHelper;
    private final IDrawable icon;

    // One arrow per distinct duration: the animation runs at the recipe's own speed,
    // and recipes that take the same time can share.
    private final Map<Integer, IDrawableAnimated> arrows = new HashMap<>();

    public TimedItemCategory(IGuiHelper guiHelper, RecipeType<RecipeHolder<R>> type, String titleKey,
            ItemStack icon) {
        this.guiHelper = guiHelper;
        this.type = type;
        this.titleKey = titleKey;
        this.icon = guiHelper.createDrawableItemStack(icon);
    }

    @Override
    public RecipeType<RecipeHolder<R>> getRecipeType() {
        return type;
    }

    @Override
    public Component getTitle() {
        return Component.translatable(titleKey);
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
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<R> holder, IFocusGroup focuses) {
        R recipe = holder.value();
        builder.addSlot(mezz.jei.api.recipe.RecipeIngredientRole.INPUT, INPUT_X, SLOT_Y)
                .addIngredients(recipe.input());
        builder.addSlot(mezz.jei.api.recipe.RecipeIngredientRole.OUTPUT, OUTPUT_X, SLOT_Y)
                .addItemStack(recipe.result());
    }

    @Override
    public void draw(RecipeHolder<R> holder, IRecipeSlotsView slots, GuiGraphics graphics, double mouseX,
            double mouseY) {
        R recipe = holder.value();
        int ticks = recipe.processingTime();
        arrows.computeIfAbsent(ticks, guiHelper::createAnimatedRecipeArrow).draw(graphics, ARROW_X, ARROW_Y);

        Font font = Minecraft.getInstance().font;
        Component time = Component.translatable(ModRecipes.TIME_KEY, JeiFormat.seconds(ticks));
        graphics.drawString(font, time, GAP_CENTER - font.width(time) / 2, TIME_Y, TIME_COLOUR, false);

        // Only worth saying when there is a fire involved at all; a drying rack has none.
        HeatLevel heat = recipe.heat();
        if (heat == HeatLevel.NONE)
            return;
        Component needs = Component.translatable(ModRecipes.NEEDS_HEAT_KEY,
                Component.translatable(heat.getTranslationKey())).withStyle(ChatFormatting.GRAY);
        graphics.drawString(font, needs, GAP_CENTER - font.width(needs) / 2, TIME_Y + 10, TIME_COLOUR, false);
    }
}
