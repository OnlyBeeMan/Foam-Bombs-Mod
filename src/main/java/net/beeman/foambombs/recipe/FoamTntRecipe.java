package net.beeman.foambombs.recipe;

import net.beeman.foambombs.FoamBombs;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class FoamTntRecipe extends CustomRecipe {

    public FoamTntRecipe() {
        super();
    }

    private boolean isSand(ItemStack stack) {
        return stack.is(Items.SAND) || stack.is(Items.RED_SAND);
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        if (input.width() != 3 || input.height() != 3) {
            return false;
        }

        // Pattern check for 3x3 TNT grid:
        // G S G (0, 1, 2)
        // S P S (3, 4, 5)
        // G S G (6, 7, 8)
        if (!input.getItem(0).is(Items.GUNPOWDER)) return false;
        if (!isSand(input.getItem(1))) return false;
        if (!input.getItem(2).is(Items.GUNPOWDER)) return false;
        if (!isSand(input.getItem(3))) return false;

        ItemStack center = input.getItem(4);
        if (!center.is(Items.LINGERING_POTION)) return false;
        PotionContents contents = center.get(DataComponents.POTION_CONTENTS);
        if (contents == null) return false;

        boolean isHealing = contents.is(Potions.HEALING) || contents.is(Potions.STRONG_HEALING);
        boolean isInvisibility = contents.is(Potions.INVISIBILITY) || contents.is(Potions.LONG_INVISIBILITY);
        boolean isPoison = contents.is(Potions.POISON) || contents.is(Potions.LONG_POISON) || contents.is(Potions.STRONG_POISON);

        if (!isHealing && !isInvisibility && !isPoison) return false;

        if (!isSand(input.getItem(5))) return false;
        if (!input.getItem(6).is(Items.GUNPOWDER)) return false;
        if (!isSand(input.getItem(7))) return false;
        if (!input.getItem(8).is(Items.GUNPOWDER)) return false;

        return true;
    }

    @Override
    public ItemStack assemble(CraftingInput input) {
        ItemStack center = input.getItem(4);
        PotionContents contents = center.get(DataComponents.POTION_CONTENTS);
        if (contents != null) {
            if (contents.is(Potions.HEALING) || contents.is(Potions.STRONG_HEALING)) {
                return new ItemStack(FoamBombs.HEALING_FOAM_TNT_REGISTRY);
            }
            if (contents.is(Potions.INVISIBILITY) || contents.is(Potions.LONG_INVISIBILITY)) {
                return new ItemStack(FoamBombs.INVISIBILITY_FOAM_TNT_REGISTRY);
            }
            if (contents.is(Potions.POISON) || contents.is(Potions.LONG_POISON) || contents.is(Potions.STRONG_POISON)) {
                return new ItemStack(FoamBombs.POISON_FOAM_TNT_REGISTRY);
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public RecipeSerializer<FoamTntRecipe> getSerializer() {
        return FoamBombs.FOAM_TNT_RECIPE_SERIALIZER;
    }
}
