package net.notaglitch.dyedepotcompat.mixin;

import com.kekecreations.arts_and_crafts.client.renderer.bewlr.ArtsAndCraftsBEWLR;
import com.kekecreations.arts_and_crafts.common.entity.DyedDecoratedPotBlockEntity;
import com.kekecreations.arts_and_crafts.common.item.DyedDecoratedPotBlockItem;
import com.kekecreations.arts_and_crafts.core.registry.ACBlocks;
import com.mojang.blaze3d.vertex.PoseStack;
import com.ninni.dye_depot.registry.DDDyes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ArtsAndCraftsBEWLR.class)
public class ArtsAndCraftsBEWLRMixin {

    @Shadow
    private DyedDecoratedPotBlockEntity dyedDecoratedPot;

    @Inject(method = "renderByItem", at = @At("HEAD"), cancellable = true)
    private void dyedepotcompat$renderDyedDecoratedPot(
            ItemStack itemStack, ItemDisplayContext displayContext,
            PoseStack poseStack, MultiBufferSource bufferSource,
            int packedLight, int packedOverlay, CallbackInfo ci) {
        if (itemStack.getItem() instanceof DyedDecoratedPotBlockItem potItem && this.dyedDecoratedPot != null) {
            Block block = potItem.getBlock();
            for (DDDyes ddDye : DDDyes.values()) {
                int dyeId = ddDye.get().getId();
                if (block == ACBlocks.getDyedDecoratedPot(dyeId)) {
                    BlockEntityRenderDispatcher dispatcher = Minecraft.getInstance().getBlockEntityRenderDispatcher();
                    this.dyedDecoratedPot.setFromItem(itemStack);
                    this.dyedDecoratedPot.setDyeColor(dyeId);
                    dispatcher.renderItem(this.dyedDecoratedPot, poseStack, bufferSource, packedLight, packedOverlay);
                    ci.cancel();
                    return;
                }
            }
        }
    }
}
