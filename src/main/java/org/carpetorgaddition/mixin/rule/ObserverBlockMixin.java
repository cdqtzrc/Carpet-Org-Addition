package org.carpetorgaddition.mixin.rule;

import net.minecraft.block.BlockState;
import net.minecraft.block.FacingBlock;
import net.minecraft.block.ObserverBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.carpetorgaddition.CarpetOrgAdditionSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ObserverBlock.class)
public abstract class ObserverBlockMixin extends FacingBlock {
    @Shadow
    protected abstract void scheduleTick(WorldAccess world, BlockPos pos);

    private ObserverBlockMixin(Settings settings) {
        super(settings);
    }

    // 可激活侦测器，打火石右键激活
    @Override
    protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (CarpetOrgAdditionSettings.canActivatesObserver) {
            ItemStack itemStack = player.getStackInHand(hand);
            if (itemStack.isOf(Items.FLINT_AND_STEEL) && !player.isSneaking()) {
                this.scheduleTick(world, pos);
                stack.damage(1, player, LivingEntity.getSlotForHand(hand));
                world.playSound(player, pos, SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.BLOCKS, 1, 1);
                player.incrementStat(Stats.USED.getOrCreateStat(itemStack.getItem()));
                return ItemActionResult.SUCCESS;
            }
        }
        return super.onUseWithItem(stack, state, world, pos, player, hand, hit);
    }
}
