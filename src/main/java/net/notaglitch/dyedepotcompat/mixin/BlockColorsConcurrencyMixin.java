package net.notaglitch.dyedepotcompat.mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;

/**
 * Prevents race conditions during asynchronous mod initialization where multiple
 * mods (such as Let's Do / Architectury mods in deferred work queues) concurrently
 * register block colors while Sodium's BlockColorsMixin is active.
 *
 * <p>Sodium's BlockColorsMixin uses an unsynchronized Reference2ReferenceOpenHashMap,
 * which throws ArrayIndexOutOfBoundsException on parallel rehash. Synchronizing
 * the register call guarantees thread safety.
 */
@Mixin(value = BlockColors.class, priority = 500)
public class BlockColorsConcurrencyMixin {

    private static final Object REGISTER_LOCK = new Object();

    @WrapMethod(method = "register")
    private void dyedepotcompat$synchronizeRegister(BlockColor blockColor, Block[] blocks, Operation<Void> original) {
        synchronized (REGISTER_LOCK) {
            original.call(blockColor, blocks);
        }
    }
}
