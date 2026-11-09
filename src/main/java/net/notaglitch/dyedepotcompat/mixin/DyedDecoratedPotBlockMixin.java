package net.notaglitch.dyedepotcompat.mixin;

import com.kekecreations.arts_and_crafts.common.block.DyedDecoratedPotBlock;
import com.kekecreations.arts_and_crafts.core.registry.ACBlocks;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DyedDecoratedPotBlock.class)
public class DyedDecoratedPotBlockMixin {

    @Inject(method = "getBlockByColor", at = @At("HEAD"), cancellable = true)
    private static void dyedepotcompat$getBlockByColor(DyeColor dyeColor, CallbackInfoReturnable<Block> cir) {
        if (dyeColor != null && dyeColor.getId() > 15) {
            Block block = ACBlocks.getDyedDecoratedPot(dyeColor.getId());
            if (block != null) {
                cir.setReturnValue(block);
            }
        }
    }
}
