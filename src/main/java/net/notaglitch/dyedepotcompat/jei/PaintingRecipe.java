package net.notaglitch.dyedepotcompat.jei;

import net.minecraft.world.item.ItemStack;
import java.util.List;

public record PaintingRecipe(List<ItemStack> inputs, ItemStack paintbrush, ItemStack output) {}
